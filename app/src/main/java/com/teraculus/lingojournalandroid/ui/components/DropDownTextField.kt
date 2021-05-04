package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView

@Composable
fun DropDownTextField(label: @Composable () -> Unit,
                      modifier: Modifier,
                      value: String,
                      leadingIcon: @Composable (() -> Unit)? = null,
                      onClick: () -> Unit) {

    val focusManager = LocalFocusManager.current
    TextField(
        label = label,
        readOnly = true,
        value = value,
        leadingIcon = leadingIcon,
        onValueChange = {},
        trailingIcon= { Icon(Icons.Rounded.ArrowDropDown, null) },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .onFocusChanged(
                onFocusChanged = {
                    if (it == FocusState.Active) {
                        onClick()
                        focusManager.clearFocus()
                    }
                },
            )
    )
}