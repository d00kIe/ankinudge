package com.teraculus.lingojournalandroid.model

import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.teraculus.lingojournalandroid.R

enum class ActivityCategory(val title: String, val color: Int = "#FFFFFFFF".toColorInt(), val icon: Int = R.drawable.ic_baseline_add_24) {
    READING("Reading", icon = R.drawable.ic_activity_reading_48, color = "#FF332288".toColorInt()),
    WRITING("Writing", icon = R.drawable.ic_activity_writing_48, color = "#FF117733".toColorInt() ),
    LISTENING("Listening", icon = R.drawable.ic_activity_listening_48, color = "#FF44AA99".toColorInt() ),
    SPEAKING("Speaking", icon = R.drawable.ic_activity_speaking_48, color = "#FF88CCEE".toColorInt() ),
    WATCHING("Watching", icon = R.drawable.ic_activity_watching_48, color = "#FFDDCC77".toColorInt() ),
    LEARNING("Learning", icon = R.drawable.ic_activity_learning_48, color = "#FFCC6677".toColorInt() ),
    EXAM("Exam", icon = R.drawable.ic_activity_exam_48, color = "#FFAA4499".toColorInt() ),
    OTHER("Other", icon = R.drawable.ic_activity_other_48, color = "#FF882255".toColorInt() )
}

