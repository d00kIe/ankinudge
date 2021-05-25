package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle

@Composable
fun Label(text: String, modifier: Modifier = Modifier.padding(horizontal = 16.dp)) {
    ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
        Text(text, modifier = modifier)
    }
}