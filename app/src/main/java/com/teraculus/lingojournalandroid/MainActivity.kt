package com.teraculus.lingojournalandroid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import com.teraculus.lingojournalandroid.ui.Main

class MainActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PickerProvider.getPickerProvider().fragmentManager = supportFragmentManager

        setContent {
            Main()
        }
    }
}