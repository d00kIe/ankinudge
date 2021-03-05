package com.teraculus.lingojournalandroid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.teraculus.lingojournalandroid.ui.Main

class MainActivity : AppCompatActivity() {
    private val pickerProvider: PickerProvider = PickerProvider(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Main(pickerProvider)
        }
    }
}