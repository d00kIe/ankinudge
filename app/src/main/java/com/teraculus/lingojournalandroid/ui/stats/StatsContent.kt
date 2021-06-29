package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.ui.calendar.CalendarSwipeable
import com.teraculus.lingojournalandroid.ui.components.ActivityRow
import com.teraculus.lingojournalandroid.ui.components.Label
import com.teraculus.lingojournalandroid.ui.components.ToggleButton
import com.teraculus.lingojournalandroid.utils.toDayString
import com.teraculus.lingojournalandroid.viewmodel.DayLanguageStreakData
import com.teraculus.lingojournalandroid.viewmodel.StatisticRange
import com.teraculus.lingojournalandroid.viewmodel.StatisticsViewModel

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
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            val tabIndex by model.rangeIndex.observeAsState(1)
            val tabs by rememberSaveable {
                mutableStateOf(listOf(StatisticRange.DAY.title,
                    StatisticRange.MONTH.title,
                    StatisticRange.YEAR.title))
            }

            val elevation =
                if (MaterialTheme.colors.isLight && (scrollState.value > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background, elevation = elevation) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
                TabRow(
                    selectedTabIndex = tabIndex,
                    backgroundColor = Color.Transparent,
                    divider = {},
                    modifier = Modifier.padding(end = 48.dp)
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
        InnerContent(model = model,
            onItemClick = onItemClick,
            modifier = modifier,
            scrollState = scrollState)
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
    scrollState: ScrollState,
) {
    val loading by model.loading.observeAsState()
    val tabIndex by model.rangeIndex.observeAsState(1)
    val languageStats by model.languageStats.observeAsState()
    val languageDayStreak by model.languageDayStreak.observeAsState()
    val day by model.day.observeAsState()
    val year by model.year.observeAsState()
    val languages by model.languages.observeAsState()
    val languageIndex by model.languageIndex.observeAsState()

    Column(modifier = modifier.verticalScroll(scrollState)) {
        AnimatedVisibility(tabIndex == 0) {
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
        AnimatedVisibility(tabIndex == 1) {
            Column {
                CalendarSwipeable(Modifier.fillMaxWidth(), model)
            }
        }
        AnimatedVisibility(tabIndex == 2) {
            Selector(Modifier.fillMaxWidth(),
                onNext = { model.setYear(year?.plusYears(1)!!) },
                onPrev = { model.setYear(year?.minusYears(1)!!) },
                hasNext = true,
                hasPrev = true) {
                Text(modifier = Modifier.padding(16.dp),
                    text = year?.value.toString(),
                    style = MaterialTheme.typography.body1
                )
            }
        }
        if (loading != true) {
            Column {
                if (languageStats != null && languageIndex != null) {
                    Column {
                        Spacer(modifier = Modifier.size(8.dp))
                        LanguageBar(languages.orEmpty(), languageIndex) { l -> model.setLanguage(l) }
                        DonutCard(stats = languageStats!!)
                        TopActivityTypes(stats = languageStats!!)
                        SentimentStatsCard(stats = languageStats!!)
                        AnimatedVisibility(visible = tabIndex == 0 && languageDayStreak != null) {
                            Column {
                                Label(text = "Streak this day",
                                        modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                                languageDayStreak?.let { DayStreakContent(it) }
                            }
                        }
                    }
                    if (tabIndex == 0) {
                        Label(text = "Activities",
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                        ActivitiesForTheDay(model = model,
                            onItemClick = onItemClick,
                            language = languageStats!!.language)
                    }
                } else {
                    Column {
                        Label(text = "No activities for this period",
                            modifier = Modifier.padding(16.dp))
                    }
                }

            }
        }
        Spacer(modifier = Modifier.size(80.dp))
    }
}

@Composable
private fun LanguageBar(
    languages: List<String>,
    languageIndex: Int?,
    onSetLanguage: (lang: String) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = languageIndex ?: 0,
        backgroundColor = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 8.dp,
        divider = {},
        indicator = {}) {
        languages.forEachIndexed { index, lang ->
            ToggleButton(onClick = { onSetLanguage(lang) },
                selected = index == languageIndex,
                modifier = Modifier.padding(8.dp) ) {
                Text(getLanguageDisplayName(lang))
            }
        }
        if(languages.isEmpty()) {
            ToggleButton(onClick = { }, selected = false,
                modifier = Modifier.padding(8.dp)) {
                Text("No activities")
            }
        }
    }
}

@Composable
private fun DayStreakContent(it: DayLanguageStreakData) {
    DayStreak(stats = it)
}


@ExperimentalMaterialApi
@Composable
private fun ActivitiesForTheDay(
    model: StatisticsViewModel,
    onItemClick: (id: String) -> Unit,
    language: String,
) {
    val activities by model.frozenActivities.observeAsState()
    Column {
        activities.orEmpty().filter { it.language == language }.forEach { activity ->
            ActivityRow(activity, onClick = onItemClick)
        }
    }
}