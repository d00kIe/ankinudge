package com.teraculus.lingojournalandroid.models

import java.time.LocalDateTime
import java.util.*

class Note(var title: String, var note: String, var dateTime: LocalDateTime, var id: String = Math.random().toString()) {
}