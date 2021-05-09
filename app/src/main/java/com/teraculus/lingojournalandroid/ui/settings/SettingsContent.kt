package com.teraculus.lingojournalandroid.ui.settings

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.BuildConfig
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ThemePreference
import com.teraculus.lingojournalandroid.ui.components.RadioSelectDialog
import com.teraculus.lingojournalandroid.utils.toTimeString
import com.teraculus.lingojournalandroid.viewmodel.cancelScheduledNotification
import com.teraculus.lingojournalandroid.viewmodel.scheduleNotification
import java.time.LocalTime


class SettingsViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val preferences = repository.getUserPreferences()
    val theme = Transformations.map(preferences) { it?.theme ?: ThemePreference.SYSTEM }
    val themeOptions = listOf(ThemePreference.DARK, ThemePreference.LIGHT, ThemePreference.SYSTEM)
    val reminderActive = Transformations.map(preferences) { it.reminderActive }
    val reminder = Transformations.map(preferences) { it.reminder }

    fun setTheme(theme: String) {
        if(themeOptions.contains(theme)) {
            repository.updateThemePreference(theme)
        }
    }

    fun setReminderActive(value: Boolean, ctx: Context) {
        if(!value)
            cancelScheduledNotification(ctx)
        else
            reminder.value?.let { scheduleNotification(ctx, it.hour, it.minute, "How was your day?", "Track your language learning activities.") }
        repository.updateReminderActivePreference(value)

    }

    fun setReminder(ctx: Context) {
        PickerProvider.getPickerProvider().pickTime("Set reminder time", reminder.value ?: LocalTime.now()
        ) {
            if(reminderActive.value == true)
                scheduleNotification(ctx, it.hour, it.minute, "How was your day?", "Track your language learning activities.")
            repository.updateReminderPreference(it)
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
    val reminderActive by viewModel.reminderActive.observeAsState()
    val context = LocalContext.current.applicationContext
    val reminder by viewModel.reminder.observeAsState()
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
        Column(modifier = Modifier.verticalScroll(scrollState).padding(horizontal = 16.dp)) {
            ListItem(text = { Text("Appearance", color = MaterialTheme.colors.primary) })
            ListItem(icon ={ Icon(Icons.Rounded.DarkMode, contentDescription = null) }, text = { Text("Theme") },
                modifier = Modifier.clickable { showThemeDialog = true })
            Spacer(modifier = Modifier.size(16.dp))
            ListItem(text = { Text("Notifications", color = MaterialTheme.colors.primary) })
            ListItem(icon = { Icon(Icons.Rounded.NotificationsActive, contentDescription = null) }, text = { Text("Reminder") }, trailing = {
                Switch(checked = reminderActive ?: false, onCheckedChange = { viewModel.setReminderActive(it, context) })
            } )
            ListItem(icon = { Icon(Icons.Rounded.Timer, contentDescription = null) }, text = { Text("Reminder time") }, trailing = {
                Text(text = toTimeString(reminder))
            }, modifier = Modifier.clickable { viewModel.setReminder(context) })
            Spacer(modifier = Modifier.size(16.dp))
            ListItem(text = { Text("About", color = MaterialTheme.colors.primary) })
            ListItem(icon ={ Icon(Icons.Rounded.Policy, contentDescription = null) }, text = { Text("Privacy policy") }, modifier = Modifier.clickable { openPrivacyPolicy() })
            ListItem(icon ={ Icon(Icons.Rounded.Mail, contentDescription = null) }, text = { Text("Feedback") }, modifier = Modifier.clickable { onOpenFeedback() })
        }
    }
}