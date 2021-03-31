package com.teraculus.lingojournalandroid.ui.stats

import android.graphics.drawable.PaintDrawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.ActivityCategory
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
fun TextStatsItem(label: String, value: String, style: TextStyle = MaterialTheme.typography.h5) {
    StatsItem(label = label) {
        Text(text = value, style = style, textAlign = TextAlign.Center)
    }
}

@Composable
fun ProgressStatsItem(label: String, value: Float, color: Color, modifier: Modifier = Modifier, strokeWidth : Dp = 16.dp) {
    val animatedValue by animateFloatAsState(targetValue = max(value / 100f, 0.01f))
    StatsItem(label = label) {
        Box() {
            CircularProgressIndicator(progress = 1f,
                color = Color.LightGray.copy(alpha = 0.2f),
                strokeWidth = strokeWidth,
                modifier = modifier.size(80.dp))
            CircularProgressIndicator(progress = animatedValue,
                color = color,
                strokeWidth = strokeWidth,
                modifier = modifier.size(80.dp))
        }
    }
}

@Composable
fun StatsCard(modifier: Modifier = Modifier, content: @Composable() () -> Unit) {
    Card(modifier = Modifier
        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
        .fillMaxWidth(), elevation = 2.dp, shape = RoundedCornerShape(8.dp), content = content)
}

@Composable
fun CombinedStatsCard(stats: LanguageStatData) {
    Column {
        StatsCard {
            Column {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextStatsItem(label = "Time", value = getActivityTimeString(stats.allMinutes))
                    TextStatsItem(label = "Activities", value = stats.allCount.toString())
                }
            }
        }

        StatsCard() {
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
        shape = RoundedCornerShape(8.dp),
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
fun CategoryCard(stats: ActivityCategoryStat) {
    StatsCard() {
        Column() {
            Text(stats.category?.title!!,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                TextStatsItem(label = "Time", value = getActivityTimeString(stats.minutes), style = MaterialTheme.typography.body1)
                TextStatsItem(label = "Activities", value = stats.count.toString(), style = MaterialTheme.typography.body1)
                ProgressStatsItem(label = "Confidence",
                    value = stats.confidence,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 4.dp)
                ProgressStatsItem(label = "Motivation",
                    value = stats.motivation,
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 4.dp)
            }
        }
    }
}

@Composable
fun DonutCard(stats: LanguageStatData) {
    var isTime: Boolean by rememberSaveable { mutableStateOf(true) }

    StatsCard() {
        Column {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = 1f,
                        color = Color.LightGray.copy(alpha = 0.2f),
                        strokeWidth = 24.dp,
                        modifier = Modifier.size(160.dp))


                    val perUnit = if (isTime) 1f / (stats.allMinutes + Float.MIN_VALUE) else 1f / (stats.allCount + Float.MIN_VALUE)
                    var currentSpan = 1f
                    stats.categoryStats.forEach {
                        CircularProgressIndicator(progress = currentSpan,
                            color = Color(it.category?.color!!),
                            strokeWidth = 24.dp,
                            modifier = Modifier.size(160.dp))
                        currentSpan -= perUnit * (if (isTime) it.minutes else it.count).toFloat()
                    }
                }
                Column(modifier = Modifier
                    .padding(16.dp)
                    .weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Column() {
                        if (stats.categoryStats.isEmpty()) {
                            DonutLegendItem(title = "None", color = Color.LightGray.copy(alpha = 0.2f).toArgb())
                        }
                        stats.categoryStats.forEach {
                            it.category?.let { it1 -> DonutLegendItem(it1.title, it1.color) }
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

@Composable
private fun DonutLegendItem(title: String, color: Int) {
    Row(modifier = Modifier.padding(top = 8.dp, start = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .background(color = Color(color),
                shape = CircleShape)
            .size(6.dp))
        Text(modifier = Modifier.padding(horizontal = 8.dp),
            text = title,
            style = MaterialTheme.typography.caption)
    }
}

@Composable
fun Selector(modifier: Modifier = Modifier, onNext: () -> Unit, onPrev: () -> Unit, hasNext: Boolean, hasPrev: Boolean, content: @Composable() () -> Unit) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onPrev, enabled = hasPrev) {
            Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = null)
        }
        content()
        IconButton(onClick = onNext, enabled = hasNext) {
            Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
fun TimeAndCountCard(stats: LanguageStatData) {
    StatsCard() {
        Column(modifier = Modifier.fillMaxWidth()) {
            TimeAndCountHeaderRow()
            TimeAndCountRow(title = "All",
                time = getActivityTimeString(stats.allMinutes),
                count = stats.allCount.toString())
            stats.categoryStats.forEach { it ->
                if (it?.category != null) {
                    TimeAndCountRow(title = it.category.title,
                        time = getActivityTimeString(it.minutes),
                        count = it.count.toString())
                }
            }
        }
    }
}
@Composable
fun TimeAndCountHeaderRow() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp), horizontalArrangement = Arrangement.End) {
        Text(text = "", modifier = Modifier.weight(0.5f))
        Text(text = "Time", modifier = Modifier.weight(0.25f), textAlign = TextAlign.Center)
        Text(text = "Count", modifier = Modifier.weight(0.25f), textAlign = TextAlign.Center)
    }
}
@Composable
fun TimeAndCountRow(title: String, time: String, count: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(text = title, modifier = Modifier.weight(0.5f))
        Text(text = time, modifier = Modifier.weight(0.25f), textAlign = TextAlign.Center)
        Text(text = count, modifier = Modifier.weight(0.25f), textAlign = TextAlign.Center)
    }
}