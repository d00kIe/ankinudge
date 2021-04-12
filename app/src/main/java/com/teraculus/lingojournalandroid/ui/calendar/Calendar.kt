package com.teraculus.lingojournalandroid.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.ui.stats.DayData
import com.teraculus.lingojournalandroid.ui.stats.MonthItemViewModel
import com.teraculus.lingojournalandroid.ui.stats.Selector
import com.teraculus.lingojournalandroid.ui.stats.StatisticsViewModel
import com.teraculus.lingojournalandroid.utils.getMinutes
import com.teraculus.lingojournalandroid.utils.getMonthForInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Float.min
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs
import kotlin.math.ceil

fun getMonthItems(): List<YearMonth> {
    val months = 20 * 12
    val thisMonth = YearMonth.now()
    val result = mutableListOf<YearMonth>()
    for (m in 0 until months) {
        result.add(0, thisMonth.minusMonths(m.toLong()))
    }

    return result
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun CalendarSwipeable(modifier: Modifier, model: StatisticsViewModel) {
    val items by remember { mutableStateOf(getMonthItems()) }
    val month by model.month.observeAsState()
    val maxMinutes by model.maxMinutes.observeAsState()
    val maxCount by model.maxCount.observeAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = items.size, initialPage = items.size - 1)

    Column() {
        Selector(modifier.fillMaxWidth(),
            onNext = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
            onPrev = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            hasNext = pagerState.currentPage < items.size - 1,
            hasPrev = pagerState.currentPage > 0) {
            MonthHeader(month = getMonthForInt(month!!.monthValue),
                year = month!!.year.toString(),
                modifier = Modifier.padding(16.dp))
        }

        LaunchedEffect(pagerState.currentPage) {
            if (month != items[pagerState.currentPage]) {
                model.setMonth(items[pagerState.currentPage])
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.height(48.dp * 5)) { page ->
            val item = items[page]
            MonthItem(item.month.value,
                item.year,
                Modifier,
                { it1 ->
                    model.setDay(LocalDate.of(it1.year, it1.month, it1.day))
                },
                maxMinutes,
                maxCount)
        }
    }
}


@Composable
fun MonthItem(
    month: Int,
    year: Int,
    modifier: Modifier,
    onClick: (data: DayData) -> Unit,
    maxMinutes: Long?,
    maxCount: Int?,
    model: MonthItemViewModel = MonthItemViewModel(yearMonth = YearMonth.of(year, month))
) {
    val dataItems by model.daydata.observeAsState()
    val weekCount = remember { mutableStateOf(ceil(dataItems.orEmpty().size / 7.0).toInt()) }
    var cellSize by remember { mutableStateOf(0.dp) }
    if (dataItems != null) {
        weekCount.value = ceil(dataItems.orEmpty().size / 7.0).toInt()
        Column(modifier = modifier.padding(horizontal = 16.dp)) {
            WeekDaysRow()
            for (w in 0 until weekCount.value) {
                BoxWithConstraints {
                    if (cellSize != maxWidth / 7) {
                        cellSize = maxWidth / 7
                    }
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {
                        for (d in 0 until 7) {
                            val itemIdx by remember { mutableStateOf(w * 7 + d) }
                            var mod = Modifier
                                .width(cellSize)
                                .height(44.dp)
                            if (dataItems.orEmpty()[itemIdx].thisMonth) {
                                mod = mod.clickable { onClick(dataItems.orEmpty()[itemIdx]) }
                            }
                            DayItem(dataItems.orEmpty()[itemIdx],
                                modifier = mod,
                                maxMinutes = maxMinutes ?: 0L,
                                maxCount = maxCount ?: 0,
                                cellSize = cellSize)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekDaysRow() {
    val days by remember { mutableStateOf(listOf("M", "T", "W", "T", "F", "S", "S")) }
    var cellSize by remember { mutableStateOf(0.dp) }
    BoxWithConstraints {
        if (cellSize != maxWidth / 7) {
            cellSize = maxWidth / 7
        }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
            for (d in days) {
                TextCell(cellText = d, cellSize = cellSize)
            }
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
    maxMinutes: Long,
    maxCount: Int,
    cellSize: Dp,
) {
    val circleSize = (20.dp) / maxCount.coerceAtLeast(1) * data.count
    val circleAlpha = min((0.35f / maxMinutes.coerceAtLeast(1)) * data.minutes, 0.35f)

    Surface(
        shape = RectangleShape,
        modifier = modifier,
        elevation = 0.dp,
    ) {
        if (data.thisMonth) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(shape = CircleShape,
                    modifier = Modifier.size(24.dp + circleSize),
                    elevation = 0.dp,
                    color = if (data.hasActivities) MaterialTheme.colors.secondary.copy(alpha = 0.65f + circleAlpha) else MaterialTheme.colors.surface) {}
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
fun TextCell(
    cellText: String,
    modifier: Modifier = Modifier,
    cellSize: Dp,
) {
    Surface(
        shape = RectangleShape,
        modifier = modifier
            .width(cellSize)
            .height(32.dp),
        elevation = 0.dp,
    ) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(cellText,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold)
        }
    }
}