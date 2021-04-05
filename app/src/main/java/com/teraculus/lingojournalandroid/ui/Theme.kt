package com.teraculus.lingojournalandroid.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ThemePreference

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

@Composable
fun LingoTheme(
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val preferences by Repository.getRepository().getUserPreferences().observeAsState()
    val colors = when(preferences?.theme) {
        ThemePreference.LIGHT -> LightColors
        ThemePreference.DARK -> DarkColors
        else ->
            if (systemDarkTheme) DarkColors else LightColors

    }
    MaterialTheme(
        colors = colors,
        content = content
    )
}