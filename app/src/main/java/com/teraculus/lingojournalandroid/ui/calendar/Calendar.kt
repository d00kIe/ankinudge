package com.teraculus.lingojournalandroid.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.utils.getMonthForInt
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
fun Calendar(modifier: Modifier, onClick: (data: DayData) -> Unit) {
    val items = getMonthItems()
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
                MonthHeader(month = getMonthForInt(currentYearMonth.monthValue),
                    year = currentYearMonth.year.toString(),
                    modifier = Modifier.padding(16.dp))
                LazyRow(state = listState) {
                    if (!listState.isScrollInProgress && listState.firstVisibleItemScrollOffset != 0) {
                        scope.launch {
                            if (listState.firstVisibleItemScrollOffset < (widthPx / 2))
                                listState.animateScrollToItem(listState.firstVisibleItemIndex)
                            else
                                listState.animateScrollToItem(listState.firstVisibleItemIndex + 1)
                        }
                    }
                    items(items) {
                        MonthItem(it.month.value, it.year, Modifier.width(width), onClick)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MonthItem() {
    MonthItem(3, 2020, Modifier.width(480.dp)) {}
}

@Composable
fun MonthItem(month: Int, year: Int, modifier: Modifier,
              onClick: (data: DayData) -> Unit) {
    val today = LocalDate.now()
    val firstDayOfMonth: LocalDate = LocalDate.of(year, month, 1)
    val lastDatOfPrevMonth = firstDayOfMonth.minusDays(1)
    val firstDayOfNextMonth: LocalDate = firstDayOfMonth.plusMonths(1)
    val dataItems = mutableListOf<DayData>()

    val dayOfWeek = DayOfWeek.from(firstDayOfMonth)
    for (i in 1 until dayOfWeek.value) {
        dataItems.add(DayData(
            0,
            lastDatOfPrevMonth.monthValue,
            lastDatOfPrevMonth.year,
            thisMonth = false,
            today = false))
    }

    val thisMonth = today.monthValue == month && today.year == year
    for (i in 1..31) {
        dataItems.add(DayData(i,
            month,
            year,
            thisMonth = true,
            today = thisMonth && today.dayOfMonth == i))
    }

    val weekCount = ceil(dataItems.size / 7.0).toInt()
    val trailingDayCount = ceil(dataItems.size / 7.0).toInt() * 7 - dataItems.size

    for (i in 1..trailingDayCount) {
        dataItems.add(DayData(0,
            firstDayOfNextMonth.monthValue,
            firstDayOfNextMonth.year,
            false,
            today = false))
    }

    Column(modifier = modifier) {
        for (w in 0 until weekCount) {
            BoxWithConstraints {
                val cellSize = maxWidth / 7
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically) {
                    for (d in 0 until 7) {
                        DayItem(dataItems[w * 7 + d], modifier = Modifier
                            .width(cellSize)
                            .height(cellSize), onClick = onClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(modifier: Modifier = Modifier, month: String, year: String) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = "$month $year",
            style = MaterialTheme.typography.subtitle2
        )
    }
}

@Preview
@Composable
fun DayItem() {
    DayItem(DayData(25, 3, 2020, thisMonth = true, today = false), onClick = {})
}

@Composable
fun DayItem(
    data: DayData,
    modifier: Modifier = Modifier,
    onClick: (data: DayData) -> Unit
) {
    Surface(shape = CircleShape, modifier = modifier, elevation = 0.dp) {
        if (!data.thisMonth)
            null
        else {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onClick(data) }) {
                Text(data.day.toString(),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    color = if (data.today) MaterialTheme.colors.secondary else Color.Unspecified)
            }
        }
    }

}