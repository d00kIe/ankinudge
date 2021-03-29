package com.teraculus.lingojournalandroid.ui.navi

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.teraculus.lingojournalandroid.R

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.journal, icon = Icons.Rounded.EventNote)
    object Stats : Screen("stats", R.string.title_stats, icon = Icons.Rounded.BarChart)
    object Settings : Screen("settings", R.string.title_settings, icon = Icons.Rounded.Settings)
}