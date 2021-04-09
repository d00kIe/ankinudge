package com.teraculus.lingojournalandroid

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.teraculus.lingojournalandroid.ui.Main
import android.view.WindowManager
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController


class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This app draws behind the system bars, so we want to handle fitting system windows
        //WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                Main(
                    onActivityClick = { launchDetailsActivity(this, it) },
                    onOpenEditor = { launchEditorActivity(this, it) }
                )
            }
        }
    }
}