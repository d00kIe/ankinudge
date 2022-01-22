package com.teraculus.lingojournalandroid.ui.components

//import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

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