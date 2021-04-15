package com.teraculus.lingojournalandroid.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.ui.stats.Constants
import com.teraculus.lingojournalandroid.ui.stats.DayData
import com.teraculus.lingojournalandroid.ui.stats.StatsCard
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.toWeekDayString
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeStatsCard(openStatsActivity:() -> Unit) {
    StatsCard(modifier = Modifier
        .padding(16.dp)
        .clickable { openStatsActivity() }) {
        Column() {            
            ListItem(
                text={ Text("Your activities")},
                secondaryText={Text("Streak and last 7 days", style=MaterialTheme.typography.body2)},
                trailing= { Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = null) }
            )
            Spacer(modifier = Modifier.size(16.dp))
            Row(modifier = Modifier.padding(16.dp)) {
                StreakText(streak = 3)
            }
        }
    }
}

@Composable
fun StreakText(streak: Int) {
    Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
//        Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Red)
//        Spacer(modifier = Modifier.size(8.dp))
        Column() {
            Text(text = "$streak days", fontWeight = FontWeight.Black, style = MaterialTheme.typography.h6, color = MaterialTheme.colors.primary)
            ApplyTextStyle(textStyle = MaterialTheme.typography.body2, contentAlpha = ContentAlpha.medium) {
                Text(text = "Streak")
            }
        }

        LastSevenDays(listOf(
            DayData(1,4,2021, true, false, true, 0, 1, 0L, 0),
            DayData(2,4,2021, true, false, false, 0, 1, 0L, 0),
            DayData(3,4,2021, true, false, true, 0, 1, 0L, 0),
            DayData(4,4,2021, true, false, false, 0, 1, 0L, 0),
            DayData(5,4,2021, true, false, true, 0, 1, 0L, 0),
            DayData(6,4,2021, true, false, false, 0, 1, 0L, 0),
            DayData(7,4,2021, true, false, true, 0, 1, 0L, 0)))
    }
}

@Composable
fun LastSevenDays(lastSevenDays: List<DayData>) {
    require(lastSevenDays.size == 7)
    Row() {
        for (d in 0 until 7) {
            Spacer(modifier = Modifier.width(4.dp))
            WeekDayItem(lastSevenDays[d])
        }
    }
}

@Composable
fun WeekDayItem(day: DayData) {
    val weekDayString = remember { getWeekDayString(day.day, day.month, day.year) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if(day.hasActivities) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colors.secondary)
        } else {
            Icon(Icons.Rounded.Circle, contentDescription = null, tint = Constants.ItemBackground)
        }
        ApplyTextStyle(textStyle = MaterialTheme.typography.body2, contentAlpha = ContentAlpha.medium) {
            Text(weekDayString)
        }
    }
}

fun getWeekDayString(day: Int, month: Int, year: Int): String {
    return toWeekDayString(LocalDate.of(year, month, day))
}