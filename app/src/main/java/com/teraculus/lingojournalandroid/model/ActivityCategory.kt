package com.teraculus.lingojournalandroid.model

import androidx.core.graphics.toColorInt
import com.teraculus.lingojournalandroid.R

enum class ActivityCategory(val title: String, val color: Int = "#FFFFFFFF".toColorInt(), val icon: Int = R.drawable.ic_baseline_add_24) {
    READING("Reading", icon = R.drawable.ic_activity_reading_48),
    WRITING("Writing", icon = R.drawable.ic_activity_writing_48 ),
    LISTENING("Listening", icon = R.drawable.ic_activity_listening_48 ),
    SPEAKING("Speaking", icon = R.drawable.ic_activity_speaking_48 ),
    WATCHING("Watching", icon = R.drawable.ic_activity_watching_48 ),
    LEARNING("Learning", icon = R.drawable.ic_activity_learning_48 ),
    EXAM("Exam", icon = R.drawable.ic_activity_exam_48 ),
    OTHER("Other", icon = R.drawable.ic_activity_other_48 )
}

