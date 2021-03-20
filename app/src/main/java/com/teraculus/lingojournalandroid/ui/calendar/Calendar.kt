package com.teraculus.lingojournalandroid.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.ceil

data class DayData(val day: Int, val thisMonth: Boolean)

@Preview
@Composable
fun Calendar(modifier: Modifier) {



    LazyColumn(modifier = modifier) {
        item {
            MonthItem(1, 2020)
        }
        item {
            MonthItem(2, 2020)
        }
        item {
            MonthItem(3, 2020)
        }
        item {
            MonthItem(4, 2020)
        }
        item {
            MonthItem(5, 2020)
        }
    }
}

@Preview
@Composable
fun MonthItem() {
    MonthItem(3, 2020)
}

@Composable
fun MonthItem(month: Int, year: Int) {
    //val yearMonthObject: YearMonth = YearMonth.of(year, month)
    val localDate: LocalDate = LocalDate.of(year, month, 1)
    val dataItems = mutableListOf<DayData>()

    val dayOfWeek = DayOfWeek.from(localDate)
    for (i in 1 until dayOfWeek.value) {
        dataItems.add(DayData(0, false))
    }

    for (i in 1..31) {
        dataItems.add(DayData(i, true))
    }
    val weekCount = ceil(dataItems.size / 7.0).toInt()
    val trailingDayCount = ceil(dataItems.size / 7.0).toInt() * 7 - dataItems.size
    for (i in 1..trailingDayCount) {
        dataItems.add(DayData(0, false))
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        for (w in 0 until weekCount) {
            BoxWithConstraints {
                val cellSize = maxWidth / 7
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically) {
                    for (d in 0 until 7) {
                        DayItem(dataItems[w * 7 + d], modifier = Modifier
                            .weight(0.14f)
                            .height(cellSize))
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(modifier: Modifier = Modifier, month: String, year: String) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.weight(1f),
            text = month,
            style = MaterialTheme.typography.h6
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = year,
            style = MaterialTheme.typography.caption
        )
    }
}

@Preview
@Composable
fun DayItem() {
    DayItem(DayData(25, true))
}

@Composable
fun DayItem(
    data: DayData,
    modifier: Modifier = Modifier,
) {
        Surface(modifier = modifier, elevation = 0.dp) {
            if (data.thisMonth)
                Text(data.day.toString(),
                    style = MaterialTheme.typography.subtitle2,
                    textAlign = TextAlign.Center)
        }

}