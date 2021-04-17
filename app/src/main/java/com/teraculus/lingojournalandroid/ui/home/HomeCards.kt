package com.teraculus.lingojournalandroid.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.ui.stats.Constants
import com.teraculus.lingojournalandroid.ui.stats.StatsCard
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.toWeekDayString
import com.teraculus.lingojournalandroid.viewmodel.DayData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeStatsCard(openStatsActivity: () -> Unit, model: ActivityListViewModel) {
    val streaks by model.streaks.observeAsState()
    val lastSevenDays by model.lastSevenDayData.observeAsState()
    val scope = rememberCoroutineScope()
    StatsCard(modifier = Modifier
        .padding(16.dp)
        .clickable { scope.launch { openStatsActivity() } }) {
        Column() {            
            ListItem(
                text={ Text("Your activities")},
                secondaryText={Text("Streak and last 7 days", style=MaterialTheme.typography.body2)},
                trailing= { Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = null) }
            )
            Column(modifier = Modifier.padding(16.dp)) {
                lastSevenDays.orEmpty().forEachIndexed() { idx: Int, it: LanguageDayData ->
                    if(idx != 0)
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                    ApplyTextStyle(textStyle = MaterialTheme.typography.body2, contentAlpha = ContentAlpha.medium) {
                        Text(getLanguageDisplayName(it.language))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    StreakText(streak = streaks.orEmpty()[it.language] ?: 0, data = it.data)
                }
            }

        }
    }
}

@Composable
fun StreakText(streak: Int, data: List<DayData>) {
    Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
//        Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Red)
//        Spacer(modifier = Modifier.size(8.dp))
        Column() {
            Text(text = "$streak ${if(streak == 1) "day" else "days"}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h6)
            ApplyTextStyle(textStyle = MaterialTheme.typography.body2, contentAlpha = ContentAlpha.medium) {
                Text(text = "Streak")
            }
        }

        LastSevenDays(lastSevenDays = data)
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
            Icon(if(day.today) Icons.Rounded.RadioButtonUnchecked else Icons.Rounded.Circle, contentDescription = null, tint = Constants.ItemBackground)
        }
        ApplyTextStyle(textStyle = MaterialTheme.typography.body2, contentAlpha = ContentAlpha.medium) {
            Text(weekDayString)
        }
    }
}

fun getWeekDayString(day: Int, month: Int, year: Int): String {
    return toWeekDayString(LocalDate.of(year, month, day)).substring(0,1)
}