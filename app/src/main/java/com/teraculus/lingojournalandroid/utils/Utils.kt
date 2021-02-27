package com.teraculus.lingojournalandroid.utils

import java.util.*

fun createDate(year: Int,
               month: Int,
               day: Int,
               hrs: Int,
               min: Int) : Date {
    val c = Calendar.getInstance()
    c.set(year, month, day, hrs, min)
    return c.time
}