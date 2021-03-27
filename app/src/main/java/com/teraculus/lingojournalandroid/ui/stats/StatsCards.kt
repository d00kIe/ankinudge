package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.utils.getActivityTimeString
import kotlin.math.max

@Composable
fun StatsItem(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.caption, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.padding(4.dp))
        content()
    }
}

@Composable
fun TextStatsItem(label: String, value: String) {
    StatsItem(label = label) {
        Text(text = value, style = MaterialTheme.typography.h5, textAlign = TextAlign.Center)
    }
}

@Composable
fun ProgressStatsItem(label: String, value: Float, color: Color) {
    val animatedValue by animateFloatAsState(targetValue = max(value / 100f, 0.01f))
    StatsItem(label = label) {
        CircularProgressIndicator(progress = animatedValue,
            color = color,
            strokeWidth = 16.dp,
            modifier = Modifier.size(80.dp))
    }
}

@Composable
fun CombinedStatsCard(stats: LanguageStatData) {
    Column {
        Card(modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .fillMaxWidth(), elevation = 4.dp, shape = RoundedCornerShape(16.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextStatsItem(label = "Time", value = getActivityTimeString(stats.allMinutes))
                    TextStatsItem(label = "Activities", value = stats.allCount.toString())
                }
            }
        }

        Card(modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .fillMaxWidth(), elevation = 4.dp, shape = RoundedCornerShape(16.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProgressStatsItem(label = "Confidence",
                        value = stats.allConfidence,
                        color = MaterialTheme.colors.primary)
                    ProgressStatsItem(label = "Motivation",
                        value = stats.allMotivation,
                        color = MaterialTheme.colors.secondary)
                }
            }
        }
    }
}

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable() () -> Unit,
) {
    val selectedColors = BorderStroke(
        ButtonDefaults.OutlinedBorderSize, MaterialTheme.colors.primary
    )
    OutlinedButton(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        onClick = onClick,
        border = when {
        isSelected -> selectedColors
        else -> ButtonDefaults.outlinedBorder
    }) {
        content()
    }
}

@Composable
fun DonutCard(stats: LanguageStatData) {
    var isTime: Boolean by rememberSaveable { mutableStateOf(true) }

    Card(modifier = Modifier
        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
        .fillMaxWidth(), elevation = 4.dp, shape = RoundedCornerShape(16.dp)) {
        Column {

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                Surface(modifier = Modifier.padding(16.dp)) {
                    val perUnit = if(isTime) 1f / stats.allMinutes else 1f / stats.allCount
                    var currentSpan = 1f
                    stats.categoryStats.forEach {
                        CircularProgressIndicator(progress = currentSpan,
                            color = Color(it.category?.color!!),
                            strokeWidth = 24.dp,
                            modifier = Modifier.size(160.dp))
                        currentSpan -= perUnit * (if(isTime) it.minutes else it.count).toFloat()
                    }
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    stats.categoryStats.forEach {
                        Row(modifier = Modifier.padding(top = 8.dp, start = 16.dp)) {
                            Box(modifier = Modifier
                                .background(color = Color(it.category?.color!!),
                                    shape = CircleShape)
                                .padding(4.dp)
                                .size(8.dp))
                            Text(modifier = Modifier.padding(horizontal = 8.dp),
                                text = it.category.title,
                                style = MaterialTheme.typography.caption)
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Chip(modifier = Modifier.padding(8.dp),
                    isSelected = isTime,
                    { if (!isTime) isTime = true }) {
                    Text("Time", style = MaterialTheme.typography.caption)
                }
                Chip(modifier = Modifier.padding(8.dp),
                    isSelected = !isTime,
                    { if (isTime) isTime = false }) {
                    Text("Count", style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}