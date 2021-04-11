package com.teraculus.lingojournalandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ThemePreference
import com.teraculus.lingojournalandroid.model.UserPreferences
import com.teraculus.lingojournalandroid.ui.DarkColors
import com.teraculus.lingojournalandroid.ui.LightColors
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.components.ActivityDetailsDialogContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import com.teraculus.lingojournalandroid.utils.initStatusBarColor
import com.teraculus.lingojournalandroid.utils.isDarkMode
import com.teraculus.lingojournalandroid.viewmodel.ActivityDetailsViewModel
import com.teraculus.lingojournalandroid.viewmodel.ActivityDetailsViewModelFactory

private const val KEY_ARG_DETAILS_ACTIVITY_ID = "KEY_ARG_DETAILS_ACTIVITY_ID"

fun launchDetailsActivity(context: Context, id: String) {
    context.startActivity(createDetailsActivityIntent(context, id))
}

fun createDetailsActivityIntent(context: Context, id: String): Intent {
    val intent = Intent(context, DetailsActivity::class.java)
    intent.putExtra(KEY_ARG_DETAILS_ACTIVITY_ID, id)
    return intent
}

data class DetailsActivityArg(
    val id: String
)

class DetailsActivity : AppCompatActivity() {
    lateinit var modelFactory : ActivityDetailsViewModelFactory
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getDetailsArgs(intent)
        modelFactory = ActivityDetailsViewModelFactory(args.id)

        Repository.getRepository().getUserPreferences().value?.let {
            initStatusBarColor(this, it)
        }

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme() {
                    val model: ActivityDetailsViewModel =
                        viewModel("activityDetailsViewModel", modelFactory)
                    ActivityDetailsDialogContent(onDismiss = { finish() },
                        onDelete = { finish() },
                        onEdit = { launchEditorActivity(this, args.id) },
                        model)
                }
            }
        }
    }

    private fun getDetailsArgs(intent: Intent): DetailsActivityArg {
        val id = intent.getStringExtra(KEY_ARG_DETAILS_ACTIVITY_ID)
        if (id.isNullOrEmpty()) {
            throw IllegalStateException("DETAILS_ACTIVITY_ID arg cannot be null or empty")
        }
        return DetailsActivityArg(id)
    }
}