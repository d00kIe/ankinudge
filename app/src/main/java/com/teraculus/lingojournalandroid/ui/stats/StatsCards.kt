package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.animation.core.animateFloatAsState
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
import com.teraculus.lingojournalandroid.utils.getDurationString
import kotlin.math.max
class Constants {
    companion object {
        val ItemBackground : Color
            @Composable
            get() {
                return if(MaterialTheme.colors.isLight)
                        Color.LightGray.copy(alpha = ContentAlpha.disabled)
                    else
                        Color.Gray.copy(alpha = ContentAlpha.disabled)
            }
    }
}

@Composable
fun StatsItem(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.overline, textAlign = TextAlign.Center)
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
fun ProgressStatsItem(label: String, value: Float, color: Color, modifier: Modifier = Modifier, strokeWidth : Dp = 4.dp) {
    val animatedValue by animateFloatAsState(targetValue = max(value / 100f, 0.01f))
    StatsItem(label = label) {
        Box() {
            CircularProgressIndicator(progress = 1f,
                color = Constants.ItemBackground,
                strokeWidth = strokeWidth,
                modifier = modifier.size(60.dp))
            CircularProgressIndicator(progress = animatedValue,
                color = color,
                strokeWidth = strokeWidth,
                modifier = modifier.size(60.dp))
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
        StatsCard() {
            Column {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProgressStatsItem(label = "Avg. Confidence",
                        value = stats.allConfidence,
                        color = MaterialTheme.colors.secondary)
                    ProgressStatsItem(label = "Avg. Motivation",
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
    OutlinedButton(
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
        onClick = onClick,
        colors = when {
            isSelected -> ButtonDefaults.outlinedButtonColors(backgroundColor = MaterialTheme.colors.surface, contentColor = MaterialTheme.colors.secondary)
            else -> ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSurface,)
        }) {
        content()
    }
}

@Composable
fun DonutCard(stats: LanguageStatData) {
    var isTime: Boolean by rememberSaveable { mutableStateOf(true) }
    val strokeWidth = 8.dp
    StatsCard() {
        Column {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = 1f,
                        color = Constants.ItemBackground,
                        strokeWidth = strokeWidth,
                        modifier = Modifier.size(120.dp))


                    val perUnit = if (isTime) 1f / (stats.allMinutes + Float.MIN_VALUE) else 1f / (stats.allCount + Float.MIN_VALUE)
                    var currentSpan = 1f
                    stats.categoryStats.forEach {
                        CircularProgressIndicator(progress = currentSpan,
                            color = Color(it.category?.color!!),
                            strokeWidth = strokeWidth,
                            modifier = Modifier.size(120.dp))
                        currentSpan -= perUnit * (if (isTime) it.minutes else it.count).toFloat()
                    }

                    Text(text = if(isTime) getDurationString(stats.allMinutes) else stats.allCount.toString())
                }
                Row(modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)) {
                    Column() {
                        if (stats.categoryStats.isEmpty()) {
                            DonutLegendItem(title = "None", color = Constants.ItemBackground.toArgb())
                        }
                        stats.categoryStats.forEach {
                            it.category?.let { it1 -> DonutLegendItem(it1.title, it1.color) }
                        }
                    }
                    Column() {
                        if (stats.categoryStats.isEmpty()) {
                            Text("--", modifier = Modifier.padding(top = 8.dp, start = 16.dp), style = MaterialTheme.typography.caption)
                        }
                        stats.categoryStats.forEach {
                            Text(if(isTime) getDurationString(it.minutes) else it.count.toString(), modifier = Modifier.padding(top = 8.dp, start = 16.dp), style = MaterialTheme.typography.caption)
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Text("Show count", style = MaterialTheme.typography.caption, modifier = Modifier.padding(horizontal = 8.dp))
                Switch(checked = !isTime, onCheckedChange = {
                    isTime = !it
                })
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
            .size(12.dp))
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
