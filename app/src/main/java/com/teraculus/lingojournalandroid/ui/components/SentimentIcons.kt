package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun selectedSentimentColor(value: Float?, expected: Float, color: Color) : Color {
    return if(value == expected) {
        color
    } else {
        Color(0x99666666)
    }
}

@Preview
@Composable
fun SentimentIcons() {
    SentimentIcons(null, {})
}

@Composable
fun SentimentIcons(value: Float?, onSentimentChange: (Float) -> Unit, color: Color = MaterialTheme.colors.primaryVariant, size: Dp = 48.dp) {
    Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(onClick = { onSentimentChange(0f) }) {
            Icon(Icons.Rounded.SentimentVeryDissatisfied, contentDescription = "verydissatisfied", Modifier.size(size), tint = selectedSentimentColor(value,0f, color))
        }

        IconButton(onClick = { onSentimentChange(25f) }) {
            Icon(Icons.Rounded.SentimentDissatisfied, contentDescription = "dissatisfied", Modifier.size(size), tint = selectedSentimentColor(value,25f, color))
        }

        IconButton(onClick = { onSentimentChange(50f) }) {
            Icon(Icons.Rounded.SentimentNeutral, contentDescription = "neutral", Modifier.size(size), tint = selectedSentimentColor(value,50f, color))
        }

        IconButton(onClick = { onSentimentChange(75f) }) {
            Icon(Icons.Rounded.SentimentSatisfiedAlt, contentDescription = "satisfied", Modifier.size(size), tint = selectedSentimentColor(value,75f, color))
        }

        IconButton(onClick = { onSentimentChange(100f) }) {
            Icon(Icons.Rounded.SentimentVerySatisfied, contentDescription = "verysatisfied", Modifier.size(size), tint = selectedSentimentColor(value,100f, color))
        }
    }
}