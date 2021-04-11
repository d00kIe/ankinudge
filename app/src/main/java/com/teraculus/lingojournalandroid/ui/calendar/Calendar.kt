package com.teraculus.lingojournalandroid.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import com.teraculus.lingojournalandroid.model.Activity
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

data class DayData(
    val day: Int,
    val month: Int,
    val year: Int,
    val thisMonth: Boolean,
    val today: Boolean,
    val hasActivities: Boolean,
    val minutes: Long,
    val count: Int,
)

fun getMonthItems(): List<YearMonth> {
    val months = 20 * 12
    val thisMonth = YearMonth.now()
    val result = mutableListOf<YearMonth>()
    for (m in 0 until months) {
        result.add(0, thisMonth.minusMonths(m.toLong()))
    }

    return result
}


fun List<Activity>.filterForDay(day: Int, month: Int, year: Int): List<Activity> {
    return this.filter { a -> a.date.year == year && a.date.monthValue == month && a.date.dayOfMonth == day }
}

fun getMonthDayData(month: Int, year: Int, activities: List<Activity>?): List<DayData> {
    val dataItems = ArrayList<DayData>()
    val today = LocalDate.now()
    val firstDayOfMonth: LocalDate = LocalDate.of(year, month, 1)
    val lastDatOfPrevMonth = firstDayOfMonth.minusDays(1)
    val firstDayOfNextMonth: LocalDate = firstDayOfMonth.plusMonths(1)
    val dayOfWeek = DayOfWeek.from(firstDayOfMonth)
    for (i in 1 until dayOfWeek.value) {
        dataItems.add(DayData(
            0,
            lastDatOfPrevMonth.monthValue,
            lastDatOfPrevMonth.year,
            thisMonth = false,
            today = false,
            hasActivities = false,
            minutes = 0,
            count = 0))
    }

    val thisMonth = today.monthValue == month && today.year == year
    for (i in 1..firstDayOfMonth.lengthOfMonth()) {
        val thisActivities = activities?.filterForDay(i, month, year)
        dataItems.add(DayData(i,
            month,
            year,
            thisMonth = true,
            today = thisMonth && today.dayOfMonth == i,
            hasActivities = !thisActivities.isNullOrEmpty()!!,
            minutes = thisActivities.orEmpty().sumOf { it-> getMinutes(it) },
            count = thisActivities.orEmpty().size))
    }


    val trailingDayCount = ceil(dataItems.size / 7.0).toInt() * 7 - dataItems.size

    for (i in 1..trailingDayCount) {
        dataItems.add(DayData(0,
            firstDayOfNextMonth.monthValue,
            firstDayOfNextMonth.year,
            thisMonth = false,
            today = false,
            hasActivities = false,
            minutes = 0,
            count = 0))
    }

    return dataItems
}

@Composable
fun Calendar(modifier: Modifier, model: StatisticsViewModel) {
    val items by remember { mutableStateOf(getMonthItems()) }
    val month by model.month.observeAsState()
    val activities by model.activities.observeAsState()
    val maxMinutes by model.maxMinutes.observeAsState()
    val maxCount by model.maxCount.observeAsState()
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = items.indexOf(month))
    val scope = rememberCoroutineScope()
    val dpToPx = with(LocalDensity.current) { 1.dp.toPx() }
    val swipeState by remember { mutableStateOf(CalendarSwipeState()) }

    BoxWithConstraints {
        val width = maxWidth
        val widthPx = width.value * dpToPx
        Surface(modifier = modifier) {
            Column {
                val currentYearMonthIdx =
                    if (listState.firstVisibleItemScrollOffset == 0) listState.firstVisibleItemIndex else listState.firstVisibleItemIndex + 1
                val currentYearMonth = items[currentYearMonthIdx]

                SideEffect {
                    if(month != currentYearMonth) {
                        model.setMonth(currentYearMonth)
                    }
                }
                Selector( modifier.fillMaxWidth(),
                    onNext = { scope.launch { listState.animateScrollToItem(listState.firstVisibleItemIndex + 1) } },
                    onPrev = { scope.launch { listState.animateScrollToItem(listState.firstVisibleItemIndex - 1) } },
                    hasNext = listState.firstVisibleItemIndex < items.size - 1,
                    hasPrev = listState.firstVisibleItemIndex > 0) {
                    MonthHeader(month = getMonthForInt(currentYearMonth.monthValue),
                        year = currentYearMonth.year.toString(),
                        modifier = Modifier.padding(16.dp))
                }
                LazyRow(state = listState) {
                    withSwipeBehaviour(listState, scope, widthPx, swipeState)
                    items(items) { it ->
                        MonthItem(it.month.value,
                            it.year,
                            Modifier.width(width),
                            { it1 ->
                                model.setDay(LocalDate.of(it1.year, it1.month, it1.day))
                            },
                            activities, !listState.isScrollInProgress && currentYearMonth == it,
                            maxMinutes!!, maxCount!!)
                    }
                }
            }
        }
    }
}

enum class SwipeDirection {
    LEFT(),
    RIGHT(),
    NONE()
}
class CalendarSwipeState() {
    private var lastOffset: Int = 0
    private var lastIndex: Int = 0
    var direction = SwipeDirection.NONE

    fun set(offset: Int, index: Int) {
        if(index == lastIndex && offset != lastOffset) {
            direction = when {
                (offset > lastOffset) -> SwipeDirection.LEFT
                (offset < lastOffset) -> SwipeDirection.RIGHT
                else -> SwipeDirection.NONE
            }
        }
        lastOffset = offset
        lastIndex = index
    }
}

private fun withSwipeBehaviour(
    listState: LazyListState,
    scope: CoroutineScope,
    widthPx: Float,
    state: CalendarSwipeState
) {
    state.set(listState.firstVisibleItemScrollOffset, listState.firstVisibleItemIndex)
    if (!listState.isScrollInProgress && listState.firstVisibleItemScrollOffset != 0) {
        scope.launch {
            if(state.direction == SwipeDirection.RIGHT) {
                if (abs(listState.firstVisibleItemScrollOffset) < (widthPx / 7) * 6)
                    listState.animateScrollToItem(listState.firstVisibleItemIndex)
                else
                    listState.animateScrollToItem(listState.firstVisibleItemIndex + 1)
            } else if (state.direction == SwipeDirection.LEFT) {
                if (abs(listState.firstVisibleItemScrollOffset) > (widthPx / 7))
                    listState.animateScrollToItem(listState.firstVisibleItemIndex + 1)
                else
                    listState.animateScrollToItem(listState.firstVisibleItemIndex)
            }
        }
    }
}


@Composable
fun MonthItem(
    month: Int,
    year: Int,
    modifier: Modifier,
    onClick: (data: DayData) -> Unit,
    activities: List<Activity>?,
    loaded: Boolean,
    maxMinutes: Long,
    maxCount: Int
) {
    val dataItems = remember {
        mutableStateOf(getMonthDayData(month,
            year,
            activities))
    }
    val weekCount = remember { mutableStateOf(ceil(dataItems.value.size / 7.0).toInt()) }
    var cellSize by remember { mutableStateOf(0.dp)}
    if (loaded) {
        dataItems.value = getMonthDayData(month,
            year,
            activities)
        weekCount.value = ceil(dataItems.value.size / 7.0).toInt()
    }
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        WeekDaysRow()
        for (w in 0 until weekCount.value) {
            BoxWithConstraints {
                if(cellSize != maxWidth / 7) {
                    cellSize = maxWidth / 7
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically) {
                    for (d in 0 until 7) {
                        val itemIdx by remember { mutableStateOf(w * 7 + d) }
                        var mod = Modifier.width(cellSize).height(44.dp)
                        if(loaded && dataItems.value[itemIdx].thisMonth) {
                            mod = mod.clickable { onClick(dataItems.value[itemIdx]) }
                        }
                        DayItem(dataItems.value[itemIdx], modifier = mod, maxMinutes = maxMinutes, maxCount = maxCount, cellSize = cellSize)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekDaysRow() {
    val days by remember { mutableStateOf(listOf("M", "T", "W", "T", "F", "S", "S")) }
    var cellSize by remember { mutableStateOf(0.dp)}
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

    Surface(shape = RectangleShape,
        modifier = modifier,
        elevation = 0.dp,
    ) {
        if (data.thisMonth) {
            Box(modifier = modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
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
    Surface(shape = RectangleShape,
        modifier = modifier.width(cellSize).height(32.dp),
        elevation = 0.dp,
    ) {
        Box(modifier = modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
            Text(cellText,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold)
        }
    }
}