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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.components.AddActivityDialogContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import com.teraculus.lingojournalandroid.utils.initStatusBarColor
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModelFactory

private const val KEY_ARG_EDITOR_ACTIVITY_ID = "KEY_ARG_EDITOR_ACTIVITY_ID"

fun launchEditorActivity(context: Context, id: String?) {
    context.startActivity(createEditorActivityIntent(context, id))
}

fun createEditorActivityIntent(context: Context, id: String?): Intent {
    val intent = Intent(context, EditorActivity::class.java)
    intent.putExtra(KEY_ARG_EDITOR_ACTIVITY_ID, id)
    return intent
}

data class EditorActivityArg(
    val id: String?
)

class EditorActivity : AppCompatActivity() {

    lateinit var modelFactory : EditActivityViewModelFactory
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getDetailsArgs(intent)

        PickerProvider.getPickerProvider().fragmentManager = supportFragmentManager
        modelFactory = EditActivityViewModelFactory(args.id, PickerProvider.getPickerProvider())

        Repository.getRepository().getUserPreferences().value?.let {
            initStatusBarColor(this, it)
        }

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme() {
                    val model : EditActivityViewModel = viewModel("editActivityViewModel", modelFactory)
                    AddActivityDialogContent(onDismiss = { finish() }, model)
                }
            }
        }
    }

    private fun getDetailsArgs(intent: Intent): EditorActivityArg {
        val id = intent.getStringExtra(KEY_ARG_EDITOR_ACTIVITY_ID)
        return EditorActivityArg(id)
    }
}