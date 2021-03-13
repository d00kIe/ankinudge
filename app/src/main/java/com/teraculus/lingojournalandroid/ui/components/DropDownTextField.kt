package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun DropDownTextField(label: @Composable () -> Unit,
                      modifier: Modifier,
                      value: String,
                      leadingIcon: @Composable (() -> Unit)? = null,
                      onClick: () -> Unit) {

    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        label = label,
        readOnly = true,
        value = value,
        leadingIcon = leadingIcon,
        onValueChange = {},
        trailingIcon= { Icon(Icons.Rounded.ArrowDropDown, null) },
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