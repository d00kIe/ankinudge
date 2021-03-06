package com.codewithdimi.ankinudge

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.codewithdimi.ankinudge.ui.AppTheme
import com.codewithdimi.ankinudge.ui.settings.SettingsContent
import com.codewithdimi.ankinudge.utils.LocalSysUiController
import com.codewithdimi.ankinudge.utils.SystemUiController
import com.codewithdimi.ankinudge.BuildConfig

fun launchSettingsActivity(context: Context) {
    context.startActivity(createSettingsActivityIntent(context))
}

fun launchPrivacyPolicyActivity(context: Context) {
    context.startActivity(Intent(Intent.ACTION_VIEW,
        Uri.parse("https://www.iubenda.com/privacy-policy/77822623")))
}

fun launchFeedbackActivity(context: Context) {
    context.startActivity(Intent(Intent.ACTION_SENDTO).apply {

        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("codewithdimi@gmail.com")) // recipients
        putExtra(Intent.EXTRA_SUBJECT, "AnkiNudge: Feedback (ver: ${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE})")
//        putExtra(Intent.EXTRA_TEXT, "Email message text")
//        putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"))
        // You can also attach multiple items by passing an ArrayList of Uris
    })
}

fun createSettingsActivityIntent(context: Context): Intent {
    return Intent(context, SettingsActivity::class.java)
}

class SettingsActivity : AppCompatActivity() {

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PickerProvider.getPickerProvider().fragmentManagerProvider = { supportFragmentManager }

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                AppTheme {
                    SettingsContent(onDismiss = { finish() },
                        onOpenFeedback = { launchFeedbackActivity(this) },
                        openPrivacyPolicy = { launchPrivacyPolicyActivity(this) })
                }
            }
        }
    }
}