package com.teraculus.lingojournalandroid.model

import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.teraculus.lingojournalandroid.R

enum class ActivityCategory(val title: String, val color: Int = "#FFFFFFFF".toColorInt(), val icon: Int = R.drawable.ic_baseline_add_24) {
    READING("Reading", icon = R.drawable.ic_activity_reading_48, color = "#FF1565c0".toColorInt()),
    WRITING("Writing", icon = R.drawable.ic_activity_writing_48, color = "#FF2196f3".toColorInt() ),
    LISTENING("Listening", icon = R.drawable.ic_activity_listening_48, color = "#FF6a1b9a".toColorInt() ),
    SPEAKING("Speaking", icon = R.drawable.ic_activity_speaking_48, color = "#FF9c27b0".toColorInt() ),
    LEARNING("Studying", icon = R.drawable.ic_activity_learning_48, color = "#FF2e7d32".toColorInt() ),
    OTHER("Other", icon = R.drawable.ic_activity_other_48, color = "#FFd84315".toColorInt() )
}

