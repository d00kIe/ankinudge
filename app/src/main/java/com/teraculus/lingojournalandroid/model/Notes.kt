package com.teraculus.lingojournalandroid.model

fun notesData(): List<Note> {
    var note = "This is a dummy note. This is a dummy note. This is a dummy note. This is a dummy note."
    return listOf(
        Note("Dummy title", note, createDate(2021, 2, 3, 12, 30)),
        Note("Dummy title", note, createDate(2021, 2, 4, 12, 30)),
        Note("Dummy title", note, createDate(2021, 2, 5, 12, 30)),
        Note("Dummy title", note, createDate(2021, 2, 6, 12, 30)),
        Note("Dummy title", note, createDate(2021, 2, 7, 12, 30)),
        Note("Dummy title", note, createDate(2021, 2, 3, 12, 30)),
        Note("Dummy title", note, createDate(2021, 2, 4, 12, 30)),
        Note("Dummy title", note, createDate(2021, 2, 5, 12, 30)),
        Note("Dummy title", note, createDate(2021, 2, 6, 12, 30)),
        Note("Dummy title", note, createDate(2021, 2, 7, 12, 30)),
    )
}