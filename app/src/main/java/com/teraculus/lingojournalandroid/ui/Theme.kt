package com.teraculus.lingojournalandroid.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ThemePreference
import com.teraculus.lingojournalandroid.utils.LocalSysUiController

private val Primary = Color(0xff00796b)
private val PrimaryVariant = Color(0xff004c40)
private val OnPrimary = Color(0xffffffff)

private val Secondary = Color(0xff00796b)
private val SecondaryVariant = Color(0xff004c40)
private val OnSecondary = Color(0xffffffff)

private val DarkColors = darkColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary,
    secondaryVariant = SecondaryVariant,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary
)
private val LightColors = lightColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary,
    secondaryVariant = SecondaryVariant,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary
)

class LingoThemeViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val preferences = repository.getUserPreferences()
    val theme = Transformations.map(preferences) { it.theme }
}

@Composable
fun LingoTheme(
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    viewModel: LingoThemeViewModel = LingoThemeViewModel(),
    content: @Composable () -> Unit
) {
    val theme by viewModel.theme.observeAsState()
    val colors = when(theme) {
        ThemePreference.LIGHT -> LightColors
        ThemePreference.DARK -> DarkColors
        else ->
            if (systemDarkTheme) DarkColors else LightColors

    }

    val sysUiController = LocalSysUiController.current
    SideEffect {
        sysUiController.setSystemBarsColor(
            color = colors.surface
        )
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}