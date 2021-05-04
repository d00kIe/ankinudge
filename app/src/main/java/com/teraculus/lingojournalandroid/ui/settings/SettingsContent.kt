package com.teraculus.lingojournalandroid.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.BuildConfig
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ThemePreference
import com.teraculus.lingojournalandroid.ui.components.RadioSelectDialog


class SettingsViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val preferences = repository.getUserPreferences()
    val theme = Transformations.map(preferences) { it?.theme ?: ThemePreference.SYSTEM }
    val themeOptions = listOf(ThemePreference.DARK, ThemePreference.LIGHT, ThemePreference.SYSTEM)

    fun setTheme(theme: String) {
        if(themeOptions.contains(theme)) {
            repository.updateThemePreference(theme)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SettingsContent(
    viewModel: SettingsViewModel = viewModel("settingsViewModel"),
    onDismiss: () -> Unit,
    openPrivacyPolicy: () -> Unit,
    onOpenFeedback: () -> Unit
) {
    val theme by viewModel.theme.observeAsState()
    val options = viewModel.themeOptions
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
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
            val elevation =
                if (MaterialTheme.colors.isLight && (scrollState.value > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
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
        },
        bottomBar = {
            Text(text = "Version: ${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    )
    {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            ListItem(icon ={ Icon(Icons.Rounded.DarkMode, contentDescription = null) }, text = { Text("Theme") },
                modifier = Modifier.clickable { showThemeDialog = true })
            ListItem(icon ={ Icon(Icons.Rounded.Policy, contentDescription = null) }, text = { Text("Privacy policy") }, modifier = Modifier.clickable { openPrivacyPolicy() })
            ListItem(icon ={ Icon(Icons.Rounded.Mail, contentDescription = null) }, text = { Text("Feedback") }, modifier = Modifier.clickable { onOpenFeedback() })
        }
    }
}