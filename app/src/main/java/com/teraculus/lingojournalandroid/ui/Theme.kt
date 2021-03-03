package com.teraculus.lingojournalandroid.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xff0c5b8f)
private val OnPrimary = Color(0xffffffff)

private val Secondary = Color(0xff0c5b8f)
private val OnSecondary = Color(0xffffffff)

private val DarkColors = darkColors(
    primary = Primary,
    secondary = Secondary,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary
)
private val LightColors = lightColors(
    primary = Primary,
    secondary = Secondary,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary
)

@Composable
fun LingoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}