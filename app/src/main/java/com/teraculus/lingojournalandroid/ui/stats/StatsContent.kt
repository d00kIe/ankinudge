package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.ui.calendar.CalendarSwipeable
import com.teraculus.lingojournalandroid.ui.components.ActivityRow
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.observeWithDelegate
import com.teraculus.lingojournalandroid.utils.toDayString

@OptIn(ExperimentalPagerApi::class)
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun StatsContent(
    modifier: Modifier = Modifier,
    model: StatisticsViewModel,
    onItemClick: (id: String) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Stats",
                        modifier = Modifier.padding(start = 24.dp),
                        style = MaterialTheme.typography.h6)
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp,
            )
        }) {
        InnerContent(model = model, onItemClick = onItemClick, modifier = modifier)
    }
}

@OptIn(ExperimentalPagerApi::class)
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun InnerContent(
    modifier: Modifier = Modifier,
    model: StatisticsViewModel,
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
    Scaffold(topBar = {
        val elevation =
            if (!MaterialTheme.colors.isLight) 0.dp else AppBarDefaults.TopAppBarElevation
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            elevation = elevation) {
            TabRow(
                selectedTabIndex = tabIndex,
                backgroundColor = MaterialTheme.colors.surface,
                divider = {},
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = index == tabIndex,
                        onClick = { model.setRangeIndex(index) }
                    )
                }
            }
        }
    }) {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            AnimatedVisibility(visible = tabIndex == 0) {
                Column {
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
                }
            }
            AnimatedVisibility(visible = tabIndex == 1) {
                Column {
                    CalendarSwipeable(Modifier.fillMaxWidth(), model)
                }
            }

            Divider()
            val notNullStats = stats.orEmpty()
            if (notNullStats.isNotEmpty()) {
                Column {
                    Spacer(modifier = Modifier.size(8.dp))
                    ScrollableTabRow(
                        selectedTabIndex = languageTab,
                        backgroundColor = MaterialTheme.colors.surface,
                        modifier = Modifier.fillMaxWidth(),
                        edgePadding = 8.dp,
                        divider = {},
                        indicator = {}) {
                        notNullStats.forEachIndexed { index, stats ->
                            if (index == languageTab) {
                                Button(onClick = { languageTab = index },
                                    modifier = Modifier.padding(8.dp),
                                    shape = RoundedCornerShape(16.dp)) {
                                    Text(getLanguageDisplayName(stats.language))
                                }
                            } else {
                                OutlinedButton(onClick = { languageTab = index },
                                    modifier = Modifier.padding(8.dp),
                                    shape = RoundedCornerShape(16.dp)) {
                                    Text(getLanguageDisplayName(stats.language))
                                }
                            }
                        }
                    }
                    AnimatedVisibility(visible = tabIndex == 0 && dayStreak.orEmpty()
                        .isNotEmpty()) {
                        Column {
                            ApplyTextStyle(textStyle = MaterialTheme.typography.caption,
                                contentAlpha = ContentAlpha.medium) {
                                Text(text = "Streak",
                                    modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                            }
                            DayStreakContent(dayStreak.orEmpty()[languageTab])
                        }
                    }
                    LanguageStatContent(notNullStats[languageTab])
                }
                if (tabIndex == 0) {
                    ApplyTextStyle(textStyle = MaterialTheme.typography.caption,
                        contentAlpha = ContentAlpha.medium) {
                        Text(text = "Activities",
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                    }
                    ActivitiesForTheDay(model = model,
                        onItemClick = onItemClick,
                        language = notNullStats[languageTab].language)
                }
            } else {
                Column {
                    ApplyTextStyle(textStyle = MaterialTheme.typography.caption,
                        contentAlpha = ContentAlpha.medium) {
                        Text(text = "No activities for this period",
                            modifier = Modifier.padding(16.dp))
                    }
                    LanguageStatContent(it = LanguageStatData.empty())
                }
            }
            Spacer(modifier = Modifier.size(80.dp))
        }
    }
}

@Composable
private fun DayStreakContent(it: DayLanguageStreakData) {
    DayStreak(stats = it)
}

@Composable
private fun LanguageStatContent(it: LanguageStatData) {
    DonutCard(stats = it)
    SentimentStatsCard(stats = it)
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