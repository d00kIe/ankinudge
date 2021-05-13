package com.teraculus.lingojournalandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.stats.StatsContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import com.teraculus.lingojournalandroid.viewmodel.StatisticsViewModel
import com.teraculus.lingojournalandroid.viewmodel.StatisticsViewModelFactory

fun launchStatsActivity(context: Context) {
    context.startActivity(createStatsActivityIntent(context))
}

fun createStatsActivityIntent(context: Context): Intent {
    return Intent(context, StatsActivity::class.java)
}

class StatsActivity : AppCompatActivity() {
    private val statsViewModel: StatisticsViewModel by viewModels { StatisticsViewModelFactory() }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme {
                    StatsContent(
                        onItemClick = { launchDetailsActivity(this, it) },
                        onDismiss = { onBackPressed() },
                        model = statsViewModel)
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