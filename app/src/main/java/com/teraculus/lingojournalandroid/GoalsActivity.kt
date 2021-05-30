package com.teraculus.lingojournalandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.goals.GoalsActivityContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController

fun launchGoalsActivity(context: Context) {
    context.startActivity(createGoalsActivityIntent(context))
}

fun createGoalsActivityIntent(context: Context): Intent {
    return Intent(context, GoalsActivity::class.java)
}

class GoalsActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PickerProvider.getPickerProvider().fragmentManagerProvider = { supportFragmentManager }
        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme {
                    GoalsActivityContent(onAddNewGoal = { launchEditGoalActivity(this) }, onDismiss = { onBackPressed() })
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            val parentIntent = Intent(this, MainActivity::class.java)
            startActivity(parentIntent)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}