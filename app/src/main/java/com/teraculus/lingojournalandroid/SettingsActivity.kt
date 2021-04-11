package com.teraculus.lingojournalandroid

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.settings.SettingsContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import com.teraculus.lingojournalandroid.utils.initStatusBarColor

fun launchSettingsActivity(context: Context) {
    context.startActivity(createSettingsActivityIntent(context))
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

        Repository.getRepository().getUserPreferences().value?.let {
            initStatusBarColor(this, it)
        }

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme {
                    SettingsContent(onDismiss = { finish() })
                }
            }
        }
    }
}