package com.codewithdimi.ankinudge.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codewithdimi.ankinudge.data.Repository
import com.codewithdimi.ankinudge.model.ThemePreference
import com.codewithdimi.ankinudge.utils.LocalSysUiController

private val Primary = Color(0xff1565c0)
private val PrimaryVariant = Color(0xff003c8f)
private val OnPrimary = Color(0xffffffff)

private val Secondary = Color(0xff1565c0)
private val SecondaryVariant = Color(0xff003c8f)
private val OnSecondary = Color(0xffffffff)


// To generate vector drawable for the icons, use: https://shapeshifter.design/
val DarkColors = darkColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary,
    secondaryVariant = SecondaryVariant,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary
)
val LightColors = lightColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary,
    secondaryVariant = SecondaryVariant,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    background = Color(0xFFFBFBFC)
)

class AppThemeViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val preferences = repository.preferences.all()
    val theme: MutableLiveData<String> = MutableLiveData(preferences.value?.theme)
    init {
        preferences.observeForever {
            theme.value = preferences.value?.theme
        }
    }
}

@Composable
fun AppTheme(
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    viewModel: AppThemeViewModel = viewModel(),
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
            color = colors.background
        )
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}