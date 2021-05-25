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
import com.teraculus.lingojournalandroid.ui.goals.AddGoalActivityContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController

private const val KEY_ARG_EDITOR_GOAL_ID = "KEY_ARG_EDITOR_GOAL_ID"

fun launchEditGoalActivity(context: Context, id: String? = null) {
    context.startActivity(createEditGoalActivityIntent(context, id))
}

fun createEditGoalActivityIntent(context: Context, id: String?): Intent {
    val intent = Intent(context, EditGoalActivity::class.java)
    intent.putExtra(KEY_ARG_EDITOR_GOAL_ID, id)
    return intent;
}


data class GoalEditorActivityArg(
    val id: String?,
)

class EditGoalActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getDetailsArgs(intent)

        PickerProvider.getPickerProvider().fragmentManagerProvider = { supportFragmentManager }
        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme {
                    AddGoalActivityContent(goalId = args.id, onDismiss = { onBackPressed() })
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

private fun getDetailsArgs(intent: Intent): GoalEditorActivityArg {
    val id = intent.getStringExtra(KEY_ARG_EDITOR_GOAL_ID)
    return GoalEditorActivityArg(id)
}