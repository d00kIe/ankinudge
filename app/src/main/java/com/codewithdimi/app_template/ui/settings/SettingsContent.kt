package com.codewithdimi.ankinudge.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import com.codewithdimi.ankinudge.BuildConfig
import com.codewithdimi.ankinudge.PickerProvider
import com.codewithdimi.ankinudge.data.Repository
import com.codewithdimi.ankinudge.model.ThemePreference
import com.codewithdimi.ankinudge.ui.components.RadioSelectDialog
import com.codewithdimi.ankinudge.utils.toTimeString
import com.codewithdimi.ankinudge.viewmodel.BillingViewModel
import com.codewithdimi.ankinudge.viewmodel.cancelScheduledNotification
import com.codewithdimi.ankinudge.viewmodel.scheduleNotification
import java.time.LocalTime


class SettingsViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val preferences = repository.preferences.all()
    val theme = Transformations.map(preferences) { it?.theme ?: ThemePreference.SYSTEM }
    val themeOptions = listOf(ThemePreference.DARK, ThemePreference.LIGHT, ThemePreference.SYSTEM)
    val reminderActive = Transformations.map(preferences) { it.reminderActive }
    val reminder = Transformations.map(preferences) { it.reminder }

    fun setTheme(theme: String) {
        if (themeOptions.contains(theme)) {
            repository.preferences.updateTheme(theme)
        }
    }

    fun setReminderActive(value: Boolean, ctx: Context) {
        if (!value)
            cancelScheduledNotification(ctx)
        else
            reminder.value?.let { scheduleNotification(ctx, it.hour, it.minute) }
        repository.preferences.updateReminderActive(value)

    }

    fun setReminder(ctx: Context) {
        PickerProvider.getPickerProvider().pickTime(
            "Set reminder time", reminder.value ?: LocalTime.now()
        ) {
            if (reminderActive.value == true)
                scheduleNotification(ctx, it.hour, it.minute)
            repository.preferences.updateReminder(it)
        }

    }

    fun showRecommendUs(activity: Activity) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Hi, I found a language learning activity tracker app that you might like. \n  https://play.google.com/store/apps/details?id=com.teraculus.lingojournalandroid&referrer=utm_source%3Drecommendation")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        activity.startActivity(shareIntent)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@Composable
fun SettingsContent(
    viewModel: SettingsViewModel = viewModel(key = "settingsViewModel"),
    billingModel: BillingViewModel = viewModel(key = "billingViewModel"),
    onDismiss: () -> Unit,
    openPrivacyPolicy: () -> Unit,
    onOpenFeedback: () -> Unit
) {
    val theme by viewModel.theme.observeAsState()
    val reminderActive by viewModel.reminderActive.observeAsState()
    val context = LocalContext.current.applicationContext
    val activity = LocalContext.current as Activity
    val reminder by viewModel.reminder.observeAsState()
    val options = viewModel.themeOptions
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val canPurchase by billingModel.canPurchase.observeAsState()

    if (showThemeDialog) {
        RadioSelectDialog(
            title = "Theme",
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
            Text(
                text = "Version: ${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    )
    {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            ListItem(text = { Text("Appearance", color = MaterialTheme.colors.primary) })
            ListItem(icon = { Icon(Icons.Rounded.DarkMode, contentDescription = null) },
                text = { Text("Theme") },
                modifier = Modifier.clickable { showThemeDialog = true })
            Spacer(modifier = Modifier.size(16.dp))
            ListItem(text = { Text("Notifications", color = MaterialTheme.colors.primary) })
            ListItem(
                icon = { Icon(Icons.Rounded.NotificationsActive, contentDescription = null) },
                text = { Text("Reminder") },
                trailing = {
                    Switch(
                        checked = reminderActive ?: false,
                        onCheckedChange = { viewModel.setReminderActive(it, context) })
                })
            ListItem(
                icon = { Icon(Icons.Rounded.Timer, contentDescription = null) },
                text = { Text("Reminder time") },
                trailing = {
                    Text(text = toTimeString(reminder))
                },
                modifier = Modifier.clickable { viewModel.setReminder(context) })
            Spacer(modifier = Modifier.size(16.dp))
            ListItem(text = { Text("Support us", color = MaterialTheme.colors.primary) })

            ListItem(
                icon = {
                    Icon(
                        Icons.Rounded.Recommend,
                        contentDescription = null
                    )
                },
                text = { Text("Recommend us") },
                modifier = Modifier.clickable { viewModel.showRecommendUs(activity) })
            
            AnimatedVisibility(visible = canPurchase == true) {
                ListItem(
                    icon = {
                        Icon(
                            Icons.Rounded.Star,
                            contentDescription = null
                        )
                    },
                    text = { Text("Upgrade to Pro") },
                    secondaryText = { Text("Remove Ads") },
                    modifier = Modifier.clickable { billingModel.tryPurchase(activity) })
            }
            
            if(canPurchase == false) {
                ListItem(
                    icon = {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary
                        )
                    },
                    text = { Text("Pro version") },
                    secondaryText = { Text("Thank you for supporting us!") },)
            }
            Spacer(modifier = Modifier.size(16.dp))
            ListItem(text = { Text("About", color = MaterialTheme.colors.primary) })
            ListItem(
                icon = { Icon(Icons.Rounded.Policy, contentDescription = null) },
                text = { Text("Privacy policy") },
                modifier = Modifier.clickable { openPrivacyPolicy() })
            ListItem(
                icon = { Icon(Icons.Rounded.Mail, contentDescription = null) },
                text = { Text("Feedback") },
                modifier = Modifier.clickable { onOpenFeedback() })
        }
    }
}