package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.teraculus.lingojournalandroid.data.Language
import com.teraculus.lingojournalandroid.data.getAllLanguages

@Composable
fun SelectDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    title: String,
    items: List<Any>,
    itemContent: @Composable (item: Any) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Card(Modifier.padding(16.dp)) {
            Column() {
                Text(text = title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(16.dp))
                Divider()
                Column(Modifier
                    .verticalScroll(rememberScrollState())) {
                    items.forEach { item ->
                        itemContent(item)
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LanguageSelectDialog(onItemClick: (item: Any) -> Unit, onDismissRequest: () -> Unit) {
    val languages by remember { mutableStateOf(getAllLanguages()) }
    SelectDialog(onDismissRequest = onDismissRequest,
        title = "Language",
        items = languages,
        itemContent = { item: Any ->
            LanguageItem(item = item,
                onClick = onItemClick)
        }
    )
}

@ExperimentalMaterialApi
@Composable
fun LanguageItem(item: Any, onClick: (item: Any) -> Unit) {
    val lang = item as Language
    ListItem(text = { Text(lang.name) }, modifier = Modifier.clickable { onClick(item) })
}