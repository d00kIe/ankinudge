package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teraculus.lingojournalandroid.ui.calendar.Calendar

@Composable
fun StatsScreen() {
    Column(Modifier.fillMaxHeight()) {

        Calendar(Modifier.weight(0.5f))
        Column(Modifier.weight(0.5f)){}
    }
}