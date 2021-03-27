package com.teraculus.lingojournalandroid.model

import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.teraculus.lingojournalandroid.R

enum class ActivityCategory(val title: String, val color: Int = "#FFFFFFFF".toColorInt(), val icon: Int = R.drawable.ic_baseline_add_24) {
    READING("Reading", icon = R.drawable.ic_activity_reading_48, color = Color.RED),
    WRITING("Writing", icon = R.drawable.ic_activity_writing_48, color = Color.BLUE ),
    LISTENING("Listening", icon = R.drawable.ic_activity_listening_48, color = Color.GREEN ),
    SPEAKING("Speaking", icon = R.drawable.ic_activity_speaking_48, color = Color.YELLOW ),
    WATCHING("Watching", icon = R.drawable.ic_activity_watching_48, color = Color.MAGENTA ),
    LEARNING("Learning", icon = R.drawable.ic_activity_learning_48, color = Color.CYAN ),
    EXAM("Exam", icon = R.drawable.ic_activity_exam_48, color = "#FF702D3D".toColorInt() ),
    OTHER("Other", icon = R.drawable.ic_activity_other_48, color = "#FFDDD884".toColorInt() )
}

