package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.ui.calendar.Calendar
import com.teraculus.lingojournalandroid.utils.observeWithDelegate
import com.teraculus.lingojournalandroid.utils.toDayString

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun StatsContent(
    modifier: Modifier = Modifier,
    model: StatisticsViewModel = viewModel("statisticsViewModel",
        StatisticsViewModelFactory()),
) {
    val tabIndex by model.rangeIndex.observeAsState(1)
    val tabs by rememberSaveable {
        mutableStateOf(listOf(StatisticRange.DAY.title,
            StatisticRange.MONTH.title,
            StatisticRange.ALL.title))
    }
    val stats by model.stats.observeAsState()
    val day by model.day.observeAsState()
    var languageTab by remember { mutableStateOf(0) }
    model.stats.observeWithDelegate {
        languageTab = 0
    }

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = tabIndex,
            backgroundColor = MaterialTheme.colors.surface) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = index == tabIndex,
                    onClick = { model.setRangeIndex(index) }
                )
            }
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AnimatedVisibility(visible = tabIndex == 0) {
                Selector(Modifier.fillMaxWidth(),
                    onNext = { model.setDay(day?.plusDays(1)!!) },
                    onPrev = { model.setDay(day?.minusDays(1)!!) },
                    hasNext = true,
                    hasPrev = true) {
                    Text(modifier = Modifier.padding(16.dp),
                        text = toDayString(day),
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            }
            AnimatedVisibility(visible = tabIndex == 1) {
                Calendar(Modifier.fillMaxWidth(), model)
            }
            val notNullStats = stats.orEmpty()
            if (notNullStats.isNotEmpty()) {
                Column {
                    TabRow(selectedTabIndex = languageTab,
                        backgroundColor = MaterialTheme.colors.surface,
                        modifier = Modifier.fillMaxWidth(),
                        indicator = { null }) {
                        notNullStats.forEachIndexed { index, stats ->
                            Tab(
                                text = { Text(getLanguageDisplayName(stats.language)) },
                                selected = index == languageTab,
                                onClick = { languageTab = index }
                            )
                        }
                    }
                    LanguageStatContent(notNullStats[languageTab])
                }
            } else {
                Column() {
                    TabRow(selectedTabIndex = 0,
                        backgroundColor = MaterialTheme.colors.surface,
                        modifier = Modifier.fillMaxWidth(),
                        indicator = { null }) {
                        Tab(
                            text = { Text("No activities") },
                            selected = true,
                            onClick = { }
                        )
                    }
                    LanguageStatContent(it = LanguageStatData.empty())
                }
            }
            Spacer(modifier = Modifier.size(80.dp))
        }
    }
}


@Composable
private fun LanguageStatContent(it: LanguageStatData) {
    DonutCard(stats = it)
    CombinedStatsCard(stats = it)
}