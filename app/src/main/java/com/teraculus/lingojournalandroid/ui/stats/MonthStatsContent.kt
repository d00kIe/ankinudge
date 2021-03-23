package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teraculus.lingojournalandroid.ui.calendar.Calendar

@Composable
fun MonthStatsContent() {
    Calendar(Modifier.fillMaxWidth(), onClick = {})
    Text("Month stats")
}