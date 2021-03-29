package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.ui.calendar.Calendar

@Composable
fun MonthStatsContent(model: StatisticsViewModel) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Calendar(Modifier.fillMaxWidth(), model)
        Divider()
        val stats by model.stats.observeAsState()

        if (stats?.isNotEmpty() == true) {
            Column {
                stats!!.forEach {
                    Text(getLanguageDisplayName(it.language),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                    DonutCard(stats = it)
                    CombinedStatsCard(stats = it)
                    it.categoryStats.forEach { it1 ->
                        CategoryCard(stats = it1)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(80.dp))
    }
}