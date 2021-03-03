package com.teraculus.lingojournalandroid.ui.navi

import androidx.annotation.StringRes
import com.teraculus.lingojournalandroid.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Home : Screen("home", R.string.title_home)
    object Calendar : Screen("calendar", R.string.title_calendar)
    object Stats : Screen("stats", R.string.title_stats)
    object Settings : Screen("settings", R.string.title_settings)
}