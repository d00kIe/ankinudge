package com.teraculus.lingojournalandroid.ui.goals

import android.util.Range
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.ui.DarkColors
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class PercentValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}%"
    }
}

class MonthValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return Month.of(value.toInt()).getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }
}

class DayValueFormatter(val month: YearMonth) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${month.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${value.toInt()}"
    }
}

class DayValueFromDateFormatter(val firstDate: LocalDate) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val date = firstDate.plusDays(value.toLong())
        return "${date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.dayOfMonth}"
    }
}

class DayOfWeekFormatter(val firstDate: LocalDate) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return firstDate.plusDays(value.toLong()).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }
}

@Composable
fun ProgressLineChart(
    color: Color = MaterialTheme.colors.secondary,
    month: YearMonth,
    values: Map<Int, Float>) {
    ProgressLineChart(
        color = color,
        values = values.mapKeys { e -> e.key.toFloat() },
        yRange = Range(0f, 100f),
        xRange = Range(1f, month.lengthOfMonth().toFloat()),
        yValueFormatter = PercentValueFormatter(),
        xValueFormatter = DayValueFormatter(month)
    )
}


@Composable
fun ProgressLineChart(
    color: Color = MaterialTheme.colors.secondary,
    firstDate: LocalDate = LocalDate.now().minusDays(29),
    dayCount: Int = 30,
    values: Map<Int, Float>) {
    ProgressLineChart(
        color = color,
        values = values.mapKeys { e -> e.key.toFloat() },
        yRange = Range(0f, 100f),
        xRange = Range(0f, dayCount.minus(1).toFloat()),
        yValueFormatter = PercentValueFormatter(),
        xValueFormatter = DayValueFromDateFormatter(firstDate)
    )
}

@Composable
fun ProgressLineChart(
    color: Color = MaterialTheme.colors.secondary,
    values: Map<Month, Float>) {
    ProgressLineChart(
        color = color,
        values = values.mapKeys { e -> e.key.value.toFloat() },
        yRange = Range(0f, 100f),
        xRange = Range(1f, 12f),
        yValueFormatter = PercentValueFormatter(),
        xValueFormatter = MonthValueFormatter()
    )
}

@Composable
fun ProgressBarChart(
    color: Color = MaterialTheme.colors.secondary,
    month: YearMonth,
    values: Map<Int, Float>) {
    ProgressBarChart(
        color = color,
        values = values.mapKeys { e -> e.value },
        yRange = Range(0f, 100f),
        xRange = Range(1f, month.lengthOfMonth().toFloat()),
        yValueFormatter = PercentValueFormatter(),
        xValueFormatter = DayValueFormatter(month)
    )
}


@Composable
fun ProgressBarChart(
    color: Color = MaterialTheme.colors.secondary,
    values: Map<Int, Float>,
    firstDate: LocalDate = LocalDate.now().minusDays(6),
    dayCount: Int = 7) {
    ProgressBarChart(
        color = color,
        values = values.mapKeys { e -> e.key.toFloat() },
        yRange = Range(0f, 100f),
        xRange = Range(0f, dayCount.minus(1).toFloat()),
        yValueFormatter = PercentValueFormatter(),
        xValueFormatter = DayOfWeekFormatter(firstDate)
    )
}

@Composable
fun ProgressLineChart(
    color: Color = MaterialTheme.colors.secondary,
    height: Dp = 100.dp,
    xRange: Range<Float> = Range(1f, 100f),
    yRange: Range<Float> = Range(0f, 100f),
    xValueFormatter: ValueFormatter = DefaultAxisValueFormatter(0),
    yValueFormatter: ValueFormatter = DefaultAxisValueFormatter(0),
    values: Map<Float, Float>,
    fillWithLastValue: Boolean = true
) {
    val alpha = ContentAlpha.medium
    val textColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    val gridColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)

    val entries: MutableList<Entry> = ArrayList()
    val sorted = values.toSortedMap()
    for (data in sorted) {
        // turn your data into Entry objects
        entries.add(Entry(data.key, data.value))
    }

    // if the last chart entry is not set, use the last possible value
    if(fillWithLastValue && entries.isNotEmpty() && !sorted.containsKey(xRange.upper)) {
        entries.add(Entry(xRange.upper, entries.last().y))
    }

    AndroidView(::LineChart, modifier = Modifier
        .fillMaxWidth()
        .height(height),
        update = { view ->
            val dataset = LineDataSet(entries, "")
            dataset.color = color.toArgb()
            dataset.setCircleColor(color.toArgb())
            dataset.setDrawCircles(false)
            dataset.setDrawCircleHole(false)
            dataset.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            dataset.fillColor = color.toArgb()
            dataset.fillAlpha = (255 * alpha).toInt()
            dataset.setDrawFilled(true)
            dataset.setDrawValues(false)
            dataset.axisDependency = YAxis.AxisDependency.RIGHT

            val xAxis = view.xAxis
            xAxis.axisMinimum = xRange.lower
            xAxis.axisMaximum = xRange.upper
            //xAxis.granularity = 100f
            xAxis.textColor = textColor.toArgb()
            xAxis.gridColor = gridColor.toArgb()
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisLineColor = gridColor.toArgb()
            xAxis.valueFormatter = xValueFormatter
            xAxis.setDrawGridLines(false)

            val yAxis = view.axisRight
            yAxis.axisMinimum = yRange.lower
            yAxis.axisMaximum = yRange.upper
            yAxis.granularity = 100f
            yAxis.textColor = textColor.toArgb()
            yAxis.gridColor = gridColor.toArgb()
            yAxis.axisLineColor = gridColor.toArgb()
            yAxis.valueFormatter = yValueFormatter
            yAxis.setDrawGridLines(true)
            yAxis.setDrawAxisLine(false)

            val yOtherAxis = view.axisLeft
            yOtherAxis.isEnabled = false

            view.legend.isEnabled = false
            view.description.isEnabled = false
            view.setTouchEnabled(false)
            view.setNoDataText("No activities")
            view.setNoDataTextColor(textColor.toArgb())
            view.minOffset = 0f
            view.extraBottomOffset = 15f
            view.extraTopOffset = 15f

            view.data = LineData(dataset)
            view.invalidate()
        })
}

@Composable
fun ProgressBarChart(
    color: Color = MaterialTheme.colors.secondary,
    height: Dp = 100.dp,
    xRange: Range<Float> = Range(1f, 100f),
    yRange: Range<Float> = Range(0f, 100f),
    xValueFormatter: ValueFormatter = DefaultAxisValueFormatter(0),
    yValueFormatter: ValueFormatter = DefaultAxisValueFormatter(0),
    values: Map<Float, Float>,
) {
    val textColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    val gridColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)

    val entries: MutableList<BarEntry> = ArrayList()
    for (data in values) {
        // turn your data into Entry objects
        entries.add(BarEntry(data.key, data.value))
    }

    AndroidView(::BarChart,
        modifier = Modifier
        .fillMaxWidth()
        .height(height),
        update = { view ->
            val dataset = BarDataSet(entries, "")
            dataset.color = color.toArgb()
            dataset.setDrawValues(false)
            dataset.axisDependency = YAxis.AxisDependency.RIGHT

            val xAxis = view.xAxis
            xAxis.axisMinimum = xRange.lower - 0.5f //to compensate cut-in-half bar
            xAxis.axisMaximum = xRange.upper + 0.5f //to compensate cut-in-half bar
            //xAxis.granularity = 100f
            xAxis.textColor = textColor.toArgb()
            xAxis.gridColor = gridColor.toArgb()
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisLineColor = gridColor.toArgb()
            xAxis.valueFormatter = xValueFormatter
            xAxis.setDrawGridLines(false)

            val yAxis = view.axisRight
            yAxis.axisMinimum = yRange.lower
            yAxis.axisMaximum = yRange.upper
            yAxis.granularity = 100f
            yAxis.textColor = textColor.toArgb()
            yAxis.gridColor = gridColor.toArgb()
            yAxis.axisLineColor = gridColor.toArgb()
            yAxis.valueFormatter = yValueFormatter
            yAxis.setDrawGridLines(true)
            yAxis.setDrawAxisLine(false)

            val yOtherAxis = view.axisLeft
            yOtherAxis.isEnabled = false

            view.legend.isEnabled = false
            view.description.isEnabled = false
            view.setTouchEnabled(false)

            view.data = BarData(dataset)
            view.data.barWidth = 0.5f
            view.setNoDataText("No activities")
            view.setNoDataTextColor(textColor.toArgb())
            view.setFitBars(true)
            view.minOffset = 0f
            view.extraBottomOffset = 15f
            view.extraTopOffset = 15f
            view.invalidate()
        })
}


//@Preview
//@Composable
//private fun ProgressBarChart() {
//    ProgressBarChart(
//        month = YearMonth.of(2021, 12),
//        values = mapOf(1 to 1f, 2 to 20f, 29 to 80f))
//}

//@Preview()
//@Composable
//private fun LongTermGoalProgressChartPreview() {
//    Card() {
//        ProgressLineChart(Color(ActivityCategory.SPEAKING.color),
//            values = mapOf(Month.JANUARY to 1f, Month.FEBRUARY to 25f, Month.DECEMBER to 50f),)
//    }
//}
//
@Preview()
@Composable
private fun LongTermGoalProgressChartPreview2() {
    MaterialTheme(colors = DarkColors) {
        Card() {
            ProgressLineChart(
                Color(ActivityCategory.SPEAKING.color),
                month = YearMonth.of(2021, 12),
                values = mapOf(1 to 1f, 4 to 4f, 5 to 55f))
        }
    }
}