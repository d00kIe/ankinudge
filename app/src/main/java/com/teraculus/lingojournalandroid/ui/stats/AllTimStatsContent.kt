package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AllTimeStatsContent(model: StatisticsViewModel) {
    val stats by model.stats.observeAsState()
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        if (stats?.isNotEmpty() == true) {
            Column {
                stats!!.forEach {
                    LanguageStatContent(it)
                }
            }
        } else {
            LanguageStatContent(it = LanguageStatData.empty())
        }
        Spacer(modifier = Modifier.size(80.dp))
    }
}

@Composable
private fun LanguageStatContent(it: LanguageStatData) {
    DonutCard(stats = it)
    CombinedStatsCard(stats = it)
    it.categoryStats.forEach { it1 ->
        CategoryCard(stats = it1)
    }
}