package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.utils.toDayString

@Composable
fun DayStatsContent(model: StatisticsViewModel) {
    val stats by model.stats.observeAsState()
    val day by model.day.observeAsState()
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Selector(Modifier.fillMaxWidth(),
            onNext = { model.setDay(day?.plusDays(1)!!) },
            onPrev = { model.setDay(day?.minusDays(1)!!) },
            hasNext = true,
            hasPrev = true) {
            Text(modifier =  Modifier.padding(16.dp),
                text = toDayString(day),
                style = MaterialTheme.typography.subtitle2
            )
        }

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
    Text(if (it.language.isNotEmpty()) getLanguageDisplayName(it.language) else "No activities",
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp))
    DonutCard(stats = it)
    CombinedStatsCard(stats = it)
    it.categoryStats.forEach { it1 ->
        CategoryCard(stats = it1)
    }
}