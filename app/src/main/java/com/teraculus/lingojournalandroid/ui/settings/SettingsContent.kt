package com.teraculus.lingojournalandroid.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun SettingsContent() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        TopAppBar() {
            Text(text = "Settings", style = MaterialTheme.typography.h6, modifier = Modifier.padding(horizontal = 16.dp))
        }
        ListItem(text = { Text("Theme")})
        Divider()
        ListItem(text = { Text("Privacy policy")})
        Divider()
    }
}