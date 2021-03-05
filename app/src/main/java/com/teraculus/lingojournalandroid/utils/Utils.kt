package com.teraculus.lingojournalandroid.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun toDateString(date: LocalDate?) : String {
    if(date == null)
        return ""
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    return date.format(dateFormatter)
}

fun toTimeString(time: LocalTime?) : String {
    if(time == null)
        return ""

    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return time.format(timeFormatter)
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

