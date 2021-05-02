package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName

@Composable
fun ToggleButton(
    onClick: () -> Unit, modifier: Modifier = Modifier,selected: Boolean, highlighted: Boolean = false, round: Boolean = false, content: @Composable RowScope.() -> Unit) {
    val selBackground = if (highlighted) MaterialTheme.colors.primary.copy(alpha = ContentAlpha.medium) else MaterialTheme.colors.surface
    val selContent = if (highlighted) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
    val outlinedButtonColors =  ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface, contentColor = MaterialTheme.colors.onSurface)
    val selectedButtonColors =  ButtonDefaults.buttonColors(backgroundColor = selBackground, contentColor = selContent)
    val shape = if(round) CircleShape else RoundedCornerShape(16.dp)
    val mod = if(round) modifier.defaultMinSize(32.dp, 32.dp) else modifier
    val padding = if(round) PaddingValues(0.dp) else ButtonDefaults.ContentPadding

    if (selected) {
        Button(onClick = onClick,
            modifier = mod,
            shape = shape,
            colors = selectedButtonColors,
            contentPadding = padding,
            content = content)
    } else {
        OutlinedButton(onClick = onClick,
            modifier = mod,
            shape = shape,
            colors = outlinedButtonColors,
            contentPadding = padding,
            content = content)
    }
}
