package com.teraculus.lingojournalandroid.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun SettingsContent() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ListItem(text = { Text("Theme")})
        Divider()
        ListItem(text = { Text("Privacy policy")})
        Divider()
    }
}