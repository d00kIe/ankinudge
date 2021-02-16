package com.teraculus.lingojournalandroid.models

import java.time.LocalDateTime
import java.util.*

fun notesData(): List<Note> {
    var note = "This is a dummy note. This is a dummy note. This is a dummy note. This is a dummy note."
    return listOf(
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 3, 12, 30), "1"),
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 4, 12, 30), "2"),
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 5, 12, 30), "3"),
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 6, 12, 30), "4"),
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 7, 12, 30), "5"),
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 3, 12, 30), "11"),
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 4, 12, 30), "21"),
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 5, 12, 30), "31"),
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 6, 12, 30), "41"),
        Note("Dummy title", note, LocalDateTime.of(2021, 2, 7, 12, 30), "51"),
    )
}