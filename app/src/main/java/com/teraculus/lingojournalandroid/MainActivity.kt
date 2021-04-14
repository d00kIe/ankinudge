package com.teraculus.lingojournalandroid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.teraculus.lingojournalandroid.ui.Main
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.ui.stats.StatisticsViewModel
import com.teraculus.lingojournalandroid.ui.stats.StatisticsViewModelFactory
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import com.teraculus.lingojournalandroid.utils.initStatusBarColor


class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                Main(
                    onActivityClick = { launchDetailsActivity(this, it) },
                    onOpenEditor = { launchEditorActivity(this, it) },
                    onOpenSettings = { launchSettingsActivity(this) },
                    onOpenStats = { launchStatsActivity(this) }
                )
            }
        }
    }
}