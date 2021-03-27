package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.ui.calendar.Calendar

// Month stats:
// Per language:
// 1. Hours
// 2. Activity count
// 3. Activity type donut - time/count
// 4. Per activity type - hours, count, average confidence / motivation, streak
@Composable
fun StatsContent(modifier: Modifier = Modifier, model: StatisticsViewModel = viewModel("statisticsViewModel", StatisticsViewModelFactory())) {
    val tabIndex by model.rangeIndex.observeAsState(1)
    val tabs = listOf(StatisticRange.DAY.title, StatisticRange.MONTH.title, StatisticRange.ALL.title)
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
        when(tabIndex) {
            0 -> DayStatsContent(model)
            1 -> MonthStatsContent(model)
            2 -> AllTimeStatsContent(model)
        }
    }
}