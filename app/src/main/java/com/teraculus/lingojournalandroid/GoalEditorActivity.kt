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
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController

private const val KEY_ARG_GOALEDITOR_ACTIVITY_ID = "KEY_ARG_GOALEDITOR_ACTIVITY_ID"

fun launchGoalEditorActivity(context: Context, id: String?) {
    context.startActivity(createGoalEditorActivityIntent(context, id))
}

fun createGoalEditorActivityIntent(context: Context, id: String?): Intent {
    val intent = Intent(context, GoalEditorActivity::class.java)
    intent.putExtra(KEY_ARG_GOALEDITOR_ACTIVITY_ID, id)
    return intent
}

data class GoalEditorActivityArg(
    val id: String?
)

class GoalEditorActivity : AppCompatActivity() {

    //lateinit var modelFactory : EditActivityViewModelFactory
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getDetailsArgs(intent)

//        PickerProvider.getPickerProvider().fragmentManager = supportFragmentManager
//        modelFactory = EditActivityViewModelFactory(args.id, PickerProvider.getPickerProvider())

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme() {
//                    val model : EditActivityViewModel = viewModel("editActivityViewModel", modelFactory)
//                    AddActivityDialogContent(onDismiss = { finish() }, model)
                }
            }
        }
    }

    private fun getDetailsArgs(intent: Intent): GoalEditorActivityArg {
        val id = intent.getStringExtra(KEY_ARG_GOALEDITOR_ACTIVITY_ID)
        return GoalEditorActivityArg(id)
    }
}