package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.teraculus.lingojournalandroid.ui.calendar.Calendar

// Month stats:
// Per language:
// 1. Hours
// 2. Activity count
// 3. Activity type donut - time/count
// 4. Per activity type - hours, count, average confidence / motivation, streak
@Composable
fun StatsContent(modifier: Modifier = Modifier) {
    var tabIndex by remember { mutableStateOf(0)}
    val tabs = listOf("Day", "Month", "All time")
    Column(modifier = modifier) {
        TabRow(selectedTabIndex = tabIndex,
            backgroundColor = MaterialTheme.colors.surface) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = index == tabIndex,
                    onClick = { tabIndex = index}
                )
            }
        }
        when(tabIndex) {
            0 -> DayStatsContent()
            1 -> MonthStatsContent()
            2 -> AllTimeStatsContent()
        }
    }
}