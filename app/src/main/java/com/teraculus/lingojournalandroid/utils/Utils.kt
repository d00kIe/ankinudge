package com.teraculus.lingojournalandroid.utils

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.LiveData
import com.teraculus.lingojournalandroid.model.Activity
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.math.abs

fun toDateString(date: LocalDate?) : String {
    if(date == null)
        return ""
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    return date.format(dateFormatter)
}

fun toDateNoYearString(date: LocalDate?) : String {
    if(date == null)
        return ""
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
    return date.format(dateFormatter)
}

fun toTimeString(time: LocalTime?) : String {
    if(time == null)
        return ""

    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return time.format(timeFormatter)
}

fun toDayString(date: LocalDate?) : String {
    if(date == null)
        return ""
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale.getDefault())
    return date.format(dateFormatter)
}

fun toRealmDateString(date: LocalDate) : String {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    return dateFormatter.format(date)
}

fun toRealmTimeString(time: LocalTime) : String {
    val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    return timeFormatter.format(time)
}

fun parseRealmDateString(string: String) : LocalDate {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    return LocalDate.parse(string, dateFormatter)
}

fun parseRealmTimeString(string: String) : LocalTime {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    return LocalTime.parse(string, dateFormatter)
}

fun localTimeToDate(localTime: LocalTime): Date {
    val calendar = Calendar.getInstance()
    calendar.clear()
    //assuming year/month/date information is not important
    calendar[0, 0, 0, localTime.hour, localTime.minute] = localTime.second
    return calendar.time
}

fun localDateToDate(localDate: LocalDate): Date {
    val calendar = Calendar.getInstance()
    calendar.clear()
    //assuming start of day
    calendar[localDate.year, localDate.monthValue - 1] = localDate.dayOfMonth
    return calendar.time
}

fun asDate(localDate: LocalDate): Date {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
}

fun asDate(localDateTime: LocalDateTime): Date {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
}

fun asLocalDate(date: Date): LocalDate {
    return Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun asLocalDateTime(date: Date): LocalDateTime {
    return Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun getMonthForInt(num: Int): String {
    var month = "wrong"
    val dfs = DateFormatSymbols()
    val months: Array<String> = dfs.months
    if (num - 1 in 0..11) {
        month = months[num - 1]
    }
    return month
}

fun getDurationString(minutes: Long): String {
    val hours = minutes / 60
    val min = minutes % 60
    val result = when {
        (hours > 0L && min > 0L) -> "${hours}h ${min}m"
        (hours == 0L && min > 0L) -> "${min}m"
        (hours > 0L && min == 0L) -> "${hours}h"
        else -> "0m"
    }

    return result
}

@Composable
fun <T> LiveData<T>.observeWithDelegate(delegate: (T) -> Unit): State<T?> = observeWithDelegate(value, delegate)

@Composable
fun <R, T : R> LiveData<T>.observeWithDelegate(initial: R, delegate: (T) -> Unit): State<R> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(initial) }
    DisposableEffect(this, lifecycleOwner) {
        val observer = Observer<T>(delegate)
        observe(lifecycleOwner, observer)
        onDispose { removeObserver(observer) }
    }
    return state
}

fun getMinutes(activity: Activity): Long {
    return getMinutes(activity.startTime, activity.endTime)
}

fun getMinutes(start: LocalTime?, end: LocalTime?): Long {
    return if(start == null || end == null)
        0
    else
        abs(Duration.between(start, end).toMinutes())
}