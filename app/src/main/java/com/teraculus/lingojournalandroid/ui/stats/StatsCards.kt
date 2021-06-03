package com.teraculus.lingojournalandroid.ui.stats

import android.util.Range
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.model.ActivityGoal
import com.teraculus.lingojournalandroid.ui.components.SentimentIcon
import com.teraculus.lingojournalandroid.ui.goals.MonthlyDailyGoalProgressChart
import com.teraculus.lingojournalandroid.ui.goals.YearlyLongTermGoalProgressChart
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.getDurationString
import com.teraculus.lingojournalandroid.viewmodel.*
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

class Constants {
    companion object {
        val ItemBackground: Color
            @Composable
            get() {
                return if (MaterialTheme.colors.isLight)
                    Color.LightGray.copy(alpha = ContentAlpha.disabled)
                else
                    Color.Gray.copy(alpha = ContentAlpha.disabled)
            }
    }
}

@Composable
fun StatsItem(
    modifier: Modifier = Modifier,
    label: String,
    bottomLabel: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (bottomLabel) {
            content()
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text = label,
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center)
        } else {
            Text(text = label,
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.padding(4.dp))
            content()
        }
    }
}

@Composable
fun TextStatsItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    style: TextStyle = MaterialTheme.typography.h5,
    bottomLabel: Boolean = false,
) {
    StatsItem(label = label, bottomLabel = bottomLabel, modifier = modifier) {
        Text(text = value, style = style, textAlign = TextAlign.Center)
    }
}

@Composable
fun StatsCard(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    content: @Composable () -> Unit,
) {
    Card(modifier = modifier, elevation = 2.dp, content = content)
}

@Composable
fun SentimentStatsCard(stats: LanguageStatData) {
    val color = if (stats.allCount == 0) Constants.ItemBackground else MaterialTheme.colors.primary
    ApplyTextStyle(textStyle = MaterialTheme.typography.caption,
        contentAlpha = ContentAlpha.medium) {
        Text(text = "Confidence & Motivation",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp))
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Card(elevation = 2.dp, modifier = Modifier
            .weight(1f)
            .padding(end = 8.dp)) {
            StatsItem(label = "Avg. Confidence") {
                SentimentIcon(value = stats.allConfidence, modifier = Modifier.size(48.dp), color)
            }
        }
        Card(elevation = 2.dp, modifier = Modifier
            .weight(1f)
            .padding(start = 8.dp)) {
            StatsItem(label = "Avg. Motivation") {
                SentimentIcon(value = stats.allMotivation, modifier = Modifier.size(48.dp), color)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DonutCard(stats: LanguageStatData) {
    var isTime: Boolean by rememberSaveable { mutableStateOf(true) }

    ApplyTextStyle(textStyle = MaterialTheme.typography.caption,
        contentAlpha = ContentAlpha.medium) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp)) {
            Text(text = "Spread")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show count",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(horizontal = 8.dp))
                Switch(checked = !isTime, onCheckedChange = {
                    isTime = !it
                })
            }
        }
    }

    StatsCard {
        Box(modifier = Modifier.padding(16.dp)) {
            SplitDonut(isTime, stats)
        }
    }
}

@Composable
private fun SplitDonut(
    isTime: Boolean,
    stats: LanguageStatData,
) {
    val strokeWidth = 8.dp
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .weight(1f),
            contentAlignment = Alignment.Center) {
            CircularProgressIndicator(progress = 1f,
                color = Constants.ItemBackground,
                strokeWidth = strokeWidth,
                modifier = Modifier.size(120.dp))


            val perUnit =
                if (isTime) 1f / (stats.allMinutes + Float.MIN_VALUE) else 1f / (stats.allCount + Float.MIN_VALUE)
            var currentSpan = 1f
            stats.categoryStats.forEach {
                CircularProgressIndicator(progress = currentSpan,
                    color = Color(it.category?.color!!),
                    strokeWidth = strokeWidth,
                    modifier = Modifier.size(120.dp))
                currentSpan -= perUnit * (if (isTime) it.minutes else it.count).toFloat()
            }

            Text(text = if (isTime) getDurationString(stats.allMinutes) else "${stats.allCount}x")
        }
        Row(modifier = Modifier
            .weight(1f)) {
            Column {
                stats.categoryStats.forEach {
                    it.category?.let { it1 -> DonutLegendItem(it1.title, it1.color) }
                }
            }
            Column {
                stats.categoryStats.forEach {
                    Text(if (isTime) getDurationString(it.minutes) else "${it.count}x",
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp),
                        style = MaterialTheme.typography.caption)
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
            .size(12.dp))
        Text(modifier = Modifier.padding(horizontal = 8.dp),
            text = title,
            style = MaterialTheme.typography.caption)
    }
}

@Composable
fun Selector(
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    hasNext: Boolean,
    hasPrev: Boolean,
    content: @Composable () -> Unit,
) {
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
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
fun DayStreak(stats: DayLanguageStreakData) {
    StatsCard {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextStatsItem(label = "Days",
                    value = stats.streak.toString(),
                    bottomLabel = true,
                    modifier = Modifier.weight(1f))
                TextStatsItem(label = "Hours",
                    value = getDurationString(stats.allMinutes),
                    bottomLabel = true,
                    modifier = Modifier.weight(1f))
                TextStatsItem(label = "Activities",
                    value = stats.allCount.toString(),
                    bottomLabel = true,
                    modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TopActivityTypes(stats: LanguageStatData) {
    if (stats.topActivityTypes.isNotEmpty()) {
        val factor = 1f / stats.topActivityTypeMinutes
        StatsHeader(text = "Top activities")
        StatsCard {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                stats.topActivityTypes.forEachIndexed { idx, it ->
                    if (idx != 0)
                        Spacer(Modifier.height(16.dp))
                    it.first?.let { at ->
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            StatsHeader(text = "${at.category?.title}: ${at.name}",
                                modifier = Modifier.padding(bottom = 8.dp))
                            StatsHeader(text = getDurationString(it.second),
                                modifier = Modifier.padding(bottom = 8.dp))
                        }
                        LinearProgressIndicator(progress = factor * it.second,
                            color = Color(at.category?.color ?: android.graphics.Color.GRAY),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatsHeader(modifier: Modifier = Modifier.padding(start = 16.dp, top = 8.dp), text: String) {
    ApplyTextStyle(textStyle = MaterialTheme.typography.caption,
        contentAlpha = ContentAlpha.medium) {
        Text(text = text, modifier = modifier)
    }
}

// Month:
// Mood bar chart per day
// Hours/count per day
// Remove average mood

// Last Year:
// Mood bar chart per month/week?
// Hours/count per month
// Remove average mood
// Longest streak ?

@Composable
fun DailyGoalsProgressChart(
    lang: String,
    month: YearMonth,
    model: AverageDailyGoalsProgressViewModel = viewModel("averageDailyGoalsProgress_${lang}_${month.toString()}",
        AverageDailyGoalsProgressViewModel.Factory(Range.create(month.atDay(1),
            month.atEndOfMonth()), lang)),
) {
    val values by model.perDayGoals.observeAsState()
    StatsCard() {
        Box(modifier = Modifier.padding(16.dp)) {
            MonthlyDailyGoalProgressChart(month = month,
                values = values.orEmpty().mapKeys { it.key.dayOfMonth })
        }
    }
}

@Composable
fun LongTermGoalsProgressCharts(
    lang: String,
    year: Year,
    goalsModel: LongTermGoalsInRangeViewModel = viewModel("longTermGoalsIn${lang}${year}",
        LongTermGoalsInRangeViewModel.Factory(Range.create(year.atDay(1),
            year.atDay(year.length())), lang)),
) {
    val goals by goalsModel.goals.observeAsState()

    Column() {
        goals?.forEach { goal ->
            LongTermGoalProgressChart(lang = lang, goal = goal, range = Range.create(year.atDay(1),
                year.atDay(year.length())), modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun LongTermGoalProgressChart(
    lang: String,
    goal: ActivityGoal,
    range: Range<LocalDate>,
    modifier: Modifier = Modifier,
    model: LongTermGoalProgressViewModel = viewModel("longTermGoalProgress${lang}_${goal.id}_${range}",
        LongTermGoalProgressViewModel.Factory(range, lang, goal)),
) {
    val perMonth by model.perMonthGoals.observeAsState()
    StatsCard(modifier = modifier) {
        Box(Modifier.padding(16.dp)) {
            YearlyLongTermGoalProgressChart(
                Color(goal.activityType?.category?.color ?: Color.Gray.toArgb()),
                values = perMonth.orEmpty().mapKeys { it.key.month },
            )
        }
    }
}