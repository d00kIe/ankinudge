package com.teraculus.lingojournalandroid.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.teraculus.lingojournalandroid.utils.getMonthForInt
import kotlinx.coroutines.launch
import java.lang.Float.min
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.ui.stats.*
import com.teraculus.lingojournalandroid.viewmodel.DayData
import com.teraculus.lingojournalandroid.viewmodel.MonthItemViewModel
import com.teraculus.lingojournalandroid.viewmodel.MonthItemViewModelFactory
import com.teraculus.lingojournalandroid.viewmodel.StatisticsViewModel
import kotlin.math.abs

private const val TAG: String = "Calendar"
private const val YEARS: Long = 20

fun getMonthItems(): List<YearMonth> {
    val months = YEARS * 12 //TODO
    val thisMonth = YearMonth.now().minusYears(YEARS / 2)
    val result = mutableListOf<YearMonth>()
    for (m in 0 until months) {
        result.add(thisMonth.plusMonths(m))
    }

    return result
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun CalendarSwipeable(modifier: Modifier, model: StatisticsViewModel) {
    val items = remember { getMonthItems() }
    val month by model.month.observeAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = items.size, initialPage = items.indexOf(month))
    Column {
        Selector(modifier.fillMaxWidth(),
            onNext = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
            onPrev = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            hasNext = pagerState.currentPage < items.size - 1,
            hasPrev = pagerState.currentPage > 0) {
            MonthHeader(month = getMonthForInt(month!!.monthValue),
                year = month!!.year.toString(),
                modifier = Modifier.padding(16.dp))
        }

        HorizontalPager(state = pagerState) { page ->
            val item = items[page]
            SideEffect {
                val page = pagerState.currentPage
                if (month != items[page]) {
                    model.setMonth(items[page])
                }
            }
            MonthItem(item.month.value,
                item.year,
                Modifier,
                { it1 ->
                    model.setDay(LocalDate.of(it1.year, it1.month, it1.day))
                })
        }
    }
}


@Composable
fun MonthItem(
    month: Int,
    year: Int,
    modifier: Modifier,
    onClick: (data: DayData) -> Unit,
    model: MonthItemViewModel = viewModel("monthItem${year}_${month}", MonthItemViewModelFactory(yearMonth = YearMonth.of(year, month))),
) {
    val dataItems by model.daydata.observeAsState()
    if (dataItems != null) {
        val weekCount = ceil(dataItems.orEmpty().size / 7.0).toInt()
        Column(modifier = modifier.padding(horizontal = 16.dp).height(48.dp * 6)) {
            WeekDaysRow()
            for (w in 0 until weekCount) {
                key(w) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {
                        for (d in 0 until 7) {
                            val itemIdx by remember { mutableStateOf(w * 7 + d) }
                            var mod = Modifier
                                .weight(1f / 7)
                                .height(44.dp)
                            val day = dataItems.orEmpty()[itemIdx]
                            if (day.thisMonth) {
                                mod = mod.clickable { onClick(day) }
                            }
                            key(day.toString()) {
                                DayItem(day, modifier = mod)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekDaysRow() {
    val days = remember { listOf("M", "T", "W", "T", "F", "S", "S") }
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
        for (d in days) {
            TextCell(cellText = d, modifier = Modifier.weight(1f/7).height(32.dp))
        }
    }
}

@Composable
private fun MonthHeader(modifier: Modifier = Modifier, month: String, year: String) {
    Text(modifier = modifier,
        text = "$month $year",
        style = MaterialTheme.typography.body1
    )
}

@Composable
fun DayItem(
    data: DayData,
    modifier: Modifier = Modifier,
) {

    Box(modifier = modifier
    ) {
        if (data.thisMonth) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                DayItemIndicator(data.hasActivities, data.maxCount, data.count, data.maxMinutes, data.minutes)
                Text(data.day.toString(),
                    style = MaterialTheme.typography.caption,
                    color = if (data.hasActivities) MaterialTheme.colors.onSecondary else Color.Unspecified,
                    textAlign = TextAlign.Center,
                    textDecoration = if (data.today) TextDecoration.Underline else null,
                    fontWeight = if (data.today) FontWeight.Black else null)
            }
        }
    }
}

@Composable
fun DayItemIndicator(show: Boolean, maxCount: Int?, count: Int, maxMinutes: Long?, minutes: Long)
{
    if(show) {
        val secondaryColor = MaterialTheme.colors.secondary
        val circleSize =
            remember { 24.dp + if (maxCount != null) (20.dp) / maxCount.coerceAtLeast(1) * count else 0.dp }
        val circleColor = remember { secondaryColor.copy(alpha = 0.65f +
            if (maxMinutes != null) min((0.35f / maxMinutes.coerceAtLeast(1)) * minutes,
                0.35f) else 0.35f)
        }

        Surface(shape = CircleShape,
            modifier = Modifier.size(circleSize),
            elevation = 0.dp,
            color = circleColor) {}
    }
}

@Composable
fun TextCell(
    cellText: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(cellText,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold)
    }
}