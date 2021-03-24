package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.ui.calendar.Calendar

@Composable
fun MonthStatsContent(model: StatisticsViewModel = viewModel("statisticsViewModel", StatisticsViewModelFactory())) {
    Calendar(Modifier.fillMaxWidth(), onClick = {})
    Divider()
    val stats by model.stats.observeAsState()

    if(stats?.isNotEmpty() == true) {
        Column() {
            Text(stats!![0].language)
            Text(stats!![0].allHours.toString())
            Text(stats!![0].allCount.toString())
            Text(stats!![0].allConfidence.toString())
            Text(stats!![0].allMotivation.toString())
        }
    }

}