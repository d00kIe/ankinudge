package com.teraculus.lingojournalandroid.ui.goals

import android.util.Range
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
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
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.ui.DarkColors
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

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
        return "${month.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${value.toInt()} "
    }
}

@Preview()
@Composable
fun LongTermGoalProgressChartPreview() {
    Card() {
        YearlyLongTermGoalProgressChart(Color(ActivityCategory.SPEAKING.color),
            values = mapOf(Month.JANUARY to 1f, Month.FEBRUARY to 25f, Month.DECEMBER to 50f),)
    }
}

@Preview()
@Composable
fun LongTermGoalProgressChartPreview2() {
    MaterialTheme(colors = DarkColors) {
        Card() {
            MonthlyLongTermGoalProgressChart(
                Color(ActivityCategory.SPEAKING.color),
                month = YearMonth.of(2021, 12),
                values = mapOf(1 to 1f, 2 to 20f, 29 to 80f))
        }
    }
}

@Composable
fun MonthlyLongTermGoalProgressChart(
    color: Color = MaterialTheme.colors.primary,
    month: YearMonth,
    values: Map<Int, Float>) {
    LongTermGoalProgressChart(
        color = color,
        values = values.mapKeys { e -> e.key.toFloat() },
        yRange = Range(0f, 100f),
        xRange = Range(1f, month.lengthOfMonth().toFloat()),
        yValueFormatter = PercentValueFormatter(),
        xValueFormatter = DayValueFormatter(month)
    )
}

@Composable
fun YearlyLongTermGoalProgressChart(
    color: Color = MaterialTheme.colors.primary,
    values: Map<Month, Float>) {
    LongTermGoalProgressChart(
        color = color,
        values = values.mapKeys { e -> e.key.value.toFloat() },
        yRange = Range(0f, 100f),
        xRange = Range(1f, 12f),
        yValueFormatter = PercentValueFormatter(),
        xValueFormatter = MonthValueFormatter()
    )
}

@Composable
fun LongTermGoalProgressChart(
    color: Color = MaterialTheme.colors.secondary,
    height: Dp = 120.dp,
    xRange: Range<Float> = Range(1f, 100f),
    yRange: Range<Float> = Range(0f, 100f),
    xValueFormatter: ValueFormatter = DefaultAxisValueFormatter(0),
    yValueFormatter: ValueFormatter = DefaultAxisValueFormatter(0),
    values: Map<Float, Float>,
) {
    val alpha = ContentAlpha.medium
    val textColor = MaterialTheme.colors.onSurface
    val gridColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)

    val entries: MutableList<Entry> = ArrayList()
    for (data in values) {
        // turn your data into Entry objects
        entries.add(Entry(data.key, data.value))
    }

    key(Random.nextInt()) {
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

                val xAxis = view.xAxis
                xAxis.axisMinimum = xRange.lower
                xAxis.axisMaximum = xRange.upper
                //xAxis.granularity = 100f
                xAxis.textColor = textColor.toArgb()
                xAxis.gridColor = gridColor.toArgb()
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.valueFormatter = xValueFormatter

                val yAxis = view.axisLeft
                yAxis.axisMinimum = yRange.lower
                yAxis.axisMaximum = yRange.upper
                //yAxis.granularity = 100f
                yAxis.textColor = textColor.toArgb()
                yAxis.gridColor = gridColor.toArgb()
                yAxis.valueFormatter = yValueFormatter

                val yRightAxis = view.axisRight
                yRightAxis.isEnabled = false

                view.legend.isEnabled = false
                view.description.isEnabled = false

                view.data = LineData(dataset)
                view.invalidate()
            })
    }
}

@Preview
@Composable
fun DailyGoalProgressChart() {
    MonthlyDailyGoalProgressChart(
        month = YearMonth.of(2021, 12),
        values = mapOf(1 to 1f, 2 to 20f, 29 to 80f))
}


@Composable
fun MonthlyDailyGoalProgressChart(
    color: Color = MaterialTheme.colors.secondary,
    month: YearMonth,
    values: Map<Int, Float>) {
    Card() {
        DailyGoalProgressChart(
            color = color,
            values = values.mapKeys { e -> e.key.toFloat() },
            yRange = Range(0f, 100f),
            xRange = Range(1f, month.lengthOfMonth().toFloat()),
            yValueFormatter = PercentValueFormatter(),
            xValueFormatter = DayValueFormatter(month)
        )
    }
}

@Composable
fun DailyGoalProgressChart(
    color: Color = MaterialTheme.colors.primary,
    height: Dp = 120.dp,
    xRange: Range<Float> = Range(1f, 100f),
    yRange: Range<Float> = Range(0f, 100f),
    xValueFormatter: ValueFormatter = DefaultAxisValueFormatter(0),
    yValueFormatter: ValueFormatter = DefaultAxisValueFormatter(0),
    values: Map<Float, Float>,
) {
    val alpha = ContentAlpha.medium
    val textColor = MaterialTheme.colors.onSurface
    val gridColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)

    val entries: MutableList<BarEntry> = ArrayList()
    for (data in values) {
        // turn your data into Entry objects
        entries.add(BarEntry(data.key, data.value))
    }

    key(Random.nextInt()) {
        AndroidView(::BarChart, modifier = Modifier
            .fillMaxWidth()
            .height(height),
            update = { view ->
                val dataset = BarDataSet(entries, "")
                dataset.color = color.toArgb()
                dataset.setDrawValues(false)

                val xAxis = view.xAxis
                xAxis.axisMinimum = xRange.lower
                xAxis.axisMaximum = xRange.upper
                //xAxis.granularity = 100f
                xAxis.textColor = textColor.toArgb()
                xAxis.gridColor = gridColor.toArgb()
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.valueFormatter = xValueFormatter

                val yAxis = view.axisLeft
                yAxis.axisMinimum = yRange.lower
                yAxis.axisMaximum = yRange.upper
                //yAxis.granularity = 100f
                yAxis.textColor = textColor.toArgb()
                yAxis.gridColor = gridColor.toArgb()
                yAxis.valueFormatter = yValueFormatter

                val yRightAxis = view.axisRight
                yRightAxis.isEnabled = false

                view.legend.isEnabled = false
                view.description.isEnabled = false

                view.data = BarData(dataset)
                view.invalidate()
            })
    }
}