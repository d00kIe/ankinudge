package com.teraculus.lingojournalandroid.model

import androidx.core.graphics.toColorInt
import com.teraculus.lingojournalandroid.R

enum class ActivityCategory(val title: String, val color: Int = "#FFFFFFFF".toColorInt(), val icon: Int = R.drawable.ic_baseline_add_24) {
    READING("Reading" ),
    WRITING("Writing" ),
    LISTENING("Listening" ),
    SPEAKING("Speaking" ),
    WATCHING("Watching" ),
    LEARNING("Learning" ),
    EXAM("Exam" ),
    OTHER("Other" )
}