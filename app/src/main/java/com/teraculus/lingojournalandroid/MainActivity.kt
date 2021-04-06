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


class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()

        setContent {
            Main(
                onActivityClick = { launchDetailsActivity(this, it) },
                onOpenEditor = { launchEditorActivity(this, it) }
            )
        }
    }


    override fun onResume() {
        super.onResume()
        if (BuildConfig.DEBUG) { // don't even consider it otherwise
//            if (Debug.isDebuggerConnected()) {
                Log.d("SCREEN",
                    "Keeping screen on for debugging, detach debugger and force an onResume to turn it off.")
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//            } else {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//                Log.d("SCREEN", "Keeping screen on for debugging is now deactivated.")
//            }
        }
    }

    private fun makeStatusBarTransparent() {
        window.statusBarColor = Color.TRANSPARENT
    }
}