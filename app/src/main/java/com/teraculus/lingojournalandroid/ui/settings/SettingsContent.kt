package com.teraculus.lingojournalandroid.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.ui.components.LanguageSelectDialog
import com.teraculus.lingojournalandroid.ui.components.ThemeSelectDialog

@ExperimentalMaterialApi
@Composable
fun SettingsContent() {
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }

    if(showThemeDialog) {
        ThemeSelectDialog(
            onDismissRequest = { showThemeDialog = false },)
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ListItem(text = { Text("Theme")}, modifier = Modifier.clickable { showThemeDialog = true })
        Divider()
        ListItem(text = { Text("Privacy policy")})
        Divider()
    }
}