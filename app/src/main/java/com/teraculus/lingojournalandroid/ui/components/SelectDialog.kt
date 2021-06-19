package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.teraculus.lingojournalandroid.data.Language
import com.teraculus.lingojournalandroid.data.getAllLanguages
import com.teraculus.lingojournalandroid.model.MeasurementUnit
import com.teraculus.lingojournalandroid.model.UserPreferences
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle

@Composable
fun SelectDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            shape = RoundedCornerShape(8.dp)) {
            Column {
                title?.let {
                    Text(text = it,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(16.dp))
                }
                content()
            }
        }
    }
}

@Composable
fun InputDialog(
    onConfirm: (value: String) -> Unit,
    onDismissRequest: () -> Unit,
    title: String,
) {
    var value by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title,
                    style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(value = value,
                    onValueChange = { value = it },
                    label = { Text("New activity type") },
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.size(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text(text = "Cancel") }
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(enabled = value.isNotBlank(),
                        onClick = { onConfirm(value) }) { Text(text = "Add") }
                }
            }
        }
    }
}

@Composable
fun NewActivityTypeDialog(
    onConfirm: (value: String, unit: MeasurementUnit) -> Unit,
    onDismissRequest: () -> Unit,
    title: String,
) {
    var value by remember { mutableStateOf("") }
    var unitValue by remember { mutableStateOf(MeasurementUnit.Time) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title,
                    style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(value = value,
                    onValueChange = { value = it },
                    label = { Text("New activity type") },
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.size(16.dp))
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text("Measurement unit")
                }
                ScrollableTabRow(
                    selectedTabIndex = MeasurementUnit.values().indexOfFirst { u -> u == unitValue }
                        .coerceAtLeast(0),
                    backgroundColor = MaterialTheme.colors.surface,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = {}) {
                    MeasurementUnit.values().forEach { unit ->
                        ToggleButton(onClick = { unitValue = unit },
                            selected = unit == unitValue,
                            modifier = Modifier.padding(8.dp),
                            highlighted = true) {
                            Text(unit.title)
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text(text = "Cancel") }
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(enabled = value.isNotBlank(),
                        onClick = { onConfirm(value, unitValue) }) { Text(text = "Add") }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LanguageSelectDialog(
    onItemClick: (item: Language) -> Unit,
    onDismissRequest: () -> Unit,
    preferences: UserPreferences?,
) {
    val languages by remember {
        mutableStateOf(getAllLanguages().filter { preferences?.languages?.contains(it.code) == false })
    }
    val usedLanguages by remember {
        mutableStateOf(getAllLanguages().filter { preferences?.languages?.contains(it.code) == true })
    }
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = "Language",
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (usedLanguages.isNotEmpty()) {
                items(usedLanguages) { item ->
                    LanguageItem(item, onClick = onItemClick, isRecent = true)
                }
                item {
                    Divider()
                }
            }
            items(languages) { item ->
                LanguageItem(item, onClick = onItemClick)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LanguageItem(lang: Language, onClick: (item: Language) -> Unit, isRecent: Boolean = false) {
    ListItem(
        text = { Text(lang.name) },
        modifier = Modifier.clickable { onClick(lang) },
        trailing = {
            if (isRecent)
                Icon(Icons.Rounded.History, contentDescription = null)
        })
}


@ExperimentalMaterialApi
@Composable
fun RadioSelectDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (text: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = title,
    ) {
        Column(Modifier.selectableGroup()) {
            options.forEach { text ->
                RadioWithText(text, text == selected, onSelect)
            }
        }
    }
}

@Composable
private fun RadioWithText(
    text: String,
    selected: Boolean,
    onSelect: (text: String) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = { onSelect(text) },
//                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null recommended for accessibility with screenreaders
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1.merge(),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun DurationPicker(
    onDismissRequest: () -> Unit,
    hours: Int?,
    minutes: Int?,
    onChange: (Int?, Int?) -> Unit,
) {
    var h by remember(hours) { mutableStateOf(hours) }
    var m by remember(minutes) { mutableStateOf(minutes) }
    SelectDialog(onDismissRequest = { onDismissRequest() }, title = "Duration") {
        Column() {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.Center) {
                NumberPicker(value = h ?: 0,
                    range = 0..1000,
                    textStyle = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    label = "h") {
                    h = it
                }
                NumberPicker(value = m ?: 0,
                    range = 0..59,
                    textStyle = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    label = "m") {
                    m = it
                }

            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismissRequest) { Text(text = "Cancel") }
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = { onChange(h, m) }) { Text(text = "Done") }
            }
        }
    }
}