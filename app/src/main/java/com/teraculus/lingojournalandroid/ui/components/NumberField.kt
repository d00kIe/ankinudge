package com.teraculus.lingojournalandroid.ui.components

import android.util.Range
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NumberField(
    modifier: Modifier = Modifier,
    value: Int?,
    onValueChange: (value: Int) -> Unit,
    range: Range<Int>?,
    label: String?,
) {
    val focusManager = LocalFocusManager.current
    var focused by remember { mutableStateOf(false) }
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    OutlinedTextField(
        value = textValue,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        label = { Text(label.orEmpty()) },
        modifier = modifier.onFocusChanged {
            if (!it.isFocused) {
                if (focused) {
                    val new = textValue.toIntOrNull()
                    val current = value ?: 0

                    // if empty or not int, take previous value
                    if (new == null) {
                        textValue = current.toString()
                    }

                    // callback
                    onValueChange(new ?: current)

                    // reset
                    focused = false
                }
            } else {
                focused = true
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions {
            focusManager.clearFocus() // this will trigger onFocusChanged
        },
        onValueChange = {
            val f: Int?
            if (it.isEmpty()) {
                textValue = it
            } else {
                f = it.toIntOrNull()
                if (f != null && value != f && (range == null || range.contains(f))) {
                    textValue = it
                }
            }
        },
        leadingIcon = {
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                    val newVal = (value ?: 0) - 1
                    if (range == null || range.contains(newVal)) {
                        onValueChange(newVal)
                    }
                }) {
                Icon(Icons.Rounded.Remove, contentDescription = null)
            }
        },
        trailingIcon = {
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                    val newVal = (value ?: 0) + 1
                    if (range == null || range.contains(newVal)) {
                        onValueChange(newVal)
                    }
                }) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        })
}