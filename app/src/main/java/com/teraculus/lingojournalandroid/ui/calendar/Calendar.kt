package com.teraculus.lingojournalandroid.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.LiveRealmResults
import com.teraculus.lingojournalandroid.ui.stats.Selector
import com.teraculus.lingojournalandroid.ui.stats.StatisticsViewModel
import com.teraculus.lingojournalandroid.utils.getMonthForInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

data class DayData(
    val day: Int,
    val month: Int,
    val year: Int,
    val thisMonth: Boolean,
    val today: Boolean,
    val hasActivities: Boolean,
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

@Composable
fun Calendar(modifier: Modifier, model: StatisticsViewModel) {
    val items by remember { mutableStateOf(getMonthItems()) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = items.size - 1)
    val scope = rememberCoroutineScope()
    val dpToPx = with(LocalDensity.current) { 1.dp.toPx() }
    val currentYearMonthIdx =
        if (listState.firstVisibleItemScrollOffset == 0) listState.firstVisibleItemIndex else listState.firstVisibleItemIndex + 1
    val currentYearMonth = items[currentYearMonthIdx]
    BoxWithConstraints {
        val width = maxWidth
        val widthPx = width.value * dpToPx
        Surface(elevation = 0.dp, modifier = modifier) {
            Column {
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
                    withSwipeBehaviour(listState, scope, widthPx)
                    items(items) { it ->
                        MonthItem(it.month.value,
                            it.year,
                            Modifier.width(width),
                            { it1 ->
                                model.setDay(LocalDate.of(it1.year, it1.month, it1.day))
                            },
                            model.activities)
                    }
                }
            }
        }
    }
}

private fun withSwipeBehaviour(
    listState: LazyListState,
    scope: CoroutineScope,
    widthPx: Float,
) {
    if (!listState.isScrollInProgress && listState.firstVisibleItemScrollOffset != 0) {
        scope.launch {
            if (listState.firstVisibleItemScrollOffset < (widthPx / 2))
                listState.animateScrollToItem(listState.firstVisibleItemIndex)
            else
                listState.animateScrollToItem(listState.firstVisibleItemIndex + 1)
        }
    }
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
            hasActivities = false))
    }

    val thisMonth = today.monthValue == month && today.year == year
    for (i in 1..firstDayOfMonth.lengthOfMonth()) {
        dataItems.add(DayData(i,
            month,
            year,
            thisMonth = true,
            today = thisMonth && today.dayOfMonth == i,
            hasActivities = activities?.filterForDay(i, month, year)?.isNotEmpty()!!))
    }


    val trailingDayCount = ceil(dataItems.size / 7.0).toInt() * 7 - dataItems.size

    for (i in 1..trailingDayCount) {
        dataItems.add(DayData(0,
            firstDayOfNextMonth.monthValue,
            firstDayOfNextMonth.year,
            thisMonth = false,
            today = false,
            hasActivities = false))
    }

    return dataItems
}

@Composable
fun MonthItem(
    month: Int, year: Int, modifier: Modifier,
    onClick: (data: DayData) -> Unit, activities: LiveRealmResults<Activity>,
) {
    val activityItems by activities.observeAsState()
    val dataItems: List<DayData> by remember {
        mutableStateOf(getMonthDayData(month,
            year,
            activityItems))
    }
    val weekCount by remember { mutableStateOf(ceil(dataItems.size / 7.0).toInt()) }
    var cellSize by remember { mutableStateOf(0.dp)}
    Column(modifier = modifier) {
        for (w in 0 until weekCount) {
            BoxWithConstraints {
                if(cellSize != maxWidth / 7) {
                    cellSize = maxWidth / 7
                }

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically) {
                    for (d in 0 until 7) {
                        val itemIdx by remember { mutableStateOf(w * 7 + d) }
                        DayItem(dataItems[itemIdx], modifier = Modifier
                            .size(cellSize)
                            .clickable { onClick(dataItems[itemIdx]) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(modifier: Modifier = Modifier, month: String, year: String) {
    Text(modifier = modifier,
        text = "$month $year",
        style = MaterialTheme.typography.subtitle2
    )
}

@Preview
@Composable
fun DayItem() {
    DayItem(DayData(25, 3, 2020, thisMonth = true, today = false, hasActivities = true))
}

@Composable
fun DayItem(
    data: DayData,
    modifier: Modifier = Modifier,
) {
    Surface(shape = RectangleShape,
        modifier = modifier,
        elevation = 0.dp,
        color = if (data.hasActivities) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    ) {
        if (data.thisMonth) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(data.day.toString(),
                            style = MaterialTheme.typography.caption,
                            textAlign = TextAlign.Center,
                            textDecoration = if (data.today) TextDecoration.Underline else null,
                            fontWeight = if (data.today) FontWeight.Bold else null)
            }
        }
    }
}