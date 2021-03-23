package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.ui.calendar.Calendar

@Composable
fun StatsScreen() {
    Column(Modifier.fillMaxHeight()) {
        StatsContent()
    }
}