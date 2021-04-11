package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.ui.calendar.Calendar
import com.teraculus.lingojournalandroid.ui.components.ActivityRow
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.observeWithDelegate
import com.teraculus.lingojournalandroid.utils.toDayString

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun StatsContent(
    modifier: Modifier = Modifier,
    model: StatisticsViewModel = viewModel("statisticsViewModel",
        StatisticsViewModelFactory()),
    onItemClick: (id: String) -> Unit,
) {
    val tabIndex by model.rangeIndex.observeAsState(1)
    val tabs by rememberSaveable {
        mutableStateOf(listOf(StatisticRange.DAY.title,
            StatisticRange.MONTH.title,
            StatisticRange.ALL.title))
    }
    val stats by model.stats.observeAsState()
    val dayStreak by model.dayStreakData.observeAsState()
    val day by model.day.observeAsState()
    var languageTab by remember { mutableStateOf(0) }
    model.stats.observeWithDelegate {
        languageTab = 0
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
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
        AnimatedVisibility(visible = tabIndex == 0) {
            Column() {
                Selector(Modifier.fillMaxWidth(),
                    onNext = { model.setDay(day?.plusDays(1)!!) },
                    onPrev = { model.setDay(day?.minusDays(1)!!) },
                    hasNext = true,
                    hasPrev = true) {
                    Text(modifier = Modifier.padding(16.dp),
                        text = toDayString(day),
                        style = MaterialTheme.typography.body1
                    )
                }
                Divider()
            }
        }
        AnimatedVisibility(visible = tabIndex == 1) {
            Column() {
                Calendar(Modifier.fillMaxWidth(), model)
                Divider()
            }
        }
        val notNullStats = stats.orEmpty()
        if (notNullStats.isNotEmpty()) {
            Column {
                TabRow(selectedTabIndex = languageTab,
                    backgroundColor = MaterialTheme.colors.surface,
                    modifier = Modifier.fillMaxWidth()) {
                    notNullStats.forEachIndexed { index, stats ->
                        Tab(
                            text = { Text(getLanguageDisplayName(stats.language)) },
                            selected = index == languageTab,
                            onClick = { languageTab = index }
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                AnimatedVisibility(visible = tabIndex == 0 && dayStreak.orEmpty().isNotEmpty()) {
                    Column() {
                        ApplyTextStyle(textStyle = MaterialTheme.typography.caption, contentAlpha = ContentAlpha.medium) {
                            Text(text = "Streak", modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                        }
                        DayStreakContent(dayStreak.orEmpty()[languageTab])
                    }
                }
                ApplyTextStyle(textStyle = MaterialTheme.typography.caption, contentAlpha = ContentAlpha.medium) {
                    Text(text = "Splits", modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                }
                LanguageStatContent(notNullStats[languageTab])
            }
            if (tabIndex == 0) {
                ApplyTextStyle(textStyle = MaterialTheme.typography.caption, contentAlpha = ContentAlpha.medium) {
                    Text(text = "Activities", modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                }
                ActivitiesForTheDay(model = model,
                    onItemClick = onItemClick,
                    language = notNullStats[languageTab].language)
            }
        } else {
            Column {
                TabRow(selectedTabIndex = 0,
                    backgroundColor = MaterialTheme.colors.surface,
                    modifier = Modifier.fillMaxWidth()) {
                    Tab(
                        text = { Text("No activities") },
                        selected = true,
                        onClick = { }
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                LanguageStatContent(it = LanguageStatData.empty())
            }
        }
        Spacer(modifier = Modifier.size(80.dp))
    }
}

@Composable
private fun DayStreakContent(it: DayLanguageStreakData) {
    DayStreak(stats = it)
}

@Composable
private fun LanguageStatContent(it: LanguageStatData) {
    DonutCard(stats = it)
    CombinedStatsCard(stats = it)
}

@ExperimentalMaterialApi
@Composable
private fun ActivitiesForTheDay(
    model: StatisticsViewModel,
    onItemClick: (id: String) -> Unit,
    language: String,
) {
    val activities by model.activities.observeAsState()
    Column {
        activities.orEmpty().filter { it.language == language }.forEach { activity ->
            ActivityRow(activity, onClick = onItemClick)
        }
    }
}