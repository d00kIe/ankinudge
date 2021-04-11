package com.teraculus.lingojournalandroid.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ThemePreference
import com.teraculus.lingojournalandroid.ui.components.RadioSelectDialog


class SettingsViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val preferences = repository.getUserPreferences()
    val theme = Transformations.map(preferences) { it.theme }
    val themeOptions = listOf(ThemePreference.DARK, ThemePreference.LIGHT, ThemePreference.SYSTEM)

    fun setTheme(theme: String) {
        if(themeOptions.contains(theme)) {
            repository.updateThemePreference(theme)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SettingsContent(viewModel: SettingsViewModel = SettingsViewModel(), onDismiss: () -> Unit) {
    val theme by viewModel.theme.observeAsState()
    val options = viewModel.themeOptions
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }

    if(showThemeDialog) {
        RadioSelectDialog(
            title="Theme",
            selected = theme.orEmpty(),
            options = options,
            onSelect = { viewModel.setTheme(it); showThemeDialog = false; },
            onDismissRequest = { showThemeDialog = false })
    }
    Scaffold(
        topBar = {
            val elevation = if(!MaterialTheme.colors.isLight) 0.dp else AppBarDefaults.TopAppBarElevation
            TopAppBar(
                title = { Text(text = "Settings") },
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation,
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    )
    {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            ListItem(text = { Text("Theme") },
                modifier = Modifier.clickable { showThemeDialog = true })
            ListItem(text = { Text("Privacy policy") })
        }
    }
}