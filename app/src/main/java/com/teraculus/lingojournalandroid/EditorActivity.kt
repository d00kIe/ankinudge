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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.components.EditActivityContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModelFactory

private const val KEY_ARG_EDITOR_ACTIVITY_ID = "KEY_ARG_EDITOR_ACTIVITY_ID"
private const val KEY_ARG_EDITOR_FROM_GOAL_ACTIVITY_ID = "KEY_ARG_EDITOR_FROM_GOAL_ACTIVITY_ID"

fun launchEditorActivity(context: Context, id: String?, goalId: String? = null) {
    context.startActivity(createEditorActivityIntent(context, id, goalId))
}

fun createEditorActivityIntent(context: Context, id: String?, goalId: String?): Intent {
    val intent = Intent(context, EditorActivity::class.java)
    intent.putExtra(KEY_ARG_EDITOR_ACTIVITY_ID, id)
    intent.putExtra(KEY_ARG_EDITOR_FROM_GOAL_ACTIVITY_ID, goalId)
    return intent
}

data class EditorActivityArg(
    val id: String?,
    val goalId: String?
)

class EditorActivity : AppCompatActivity() {

    lateinit var modelFactory : EditActivityViewModelFactory
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getDetailsArgs(intent)

        PickerProvider.getPickerProvider().fragmentManagerProvider = { supportFragmentManager }
        modelFactory = EditActivityViewModelFactory(args.id, args.goalId, PickerProvider.getPickerProvider())

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme {
                    EditActivityContent(onDismiss = { onBackPressed() }, model = viewModel(key = "editActivityViewModel",  factory = modelFactory))
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

    private fun getDetailsArgs(intent: Intent): EditorActivityArg {
        val id = intent.getStringExtra(KEY_ARG_EDITOR_ACTIVITY_ID)
        val goalId = intent.getStringExtra(KEY_ARG_EDITOR_FROM_GOAL_ACTIVITY_ID)
        return EditorActivityArg(id,goalId)
    }
}