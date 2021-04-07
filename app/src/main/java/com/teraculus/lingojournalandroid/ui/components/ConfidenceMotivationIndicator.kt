package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Preview
@Composable
fun PreviewConfidenceMotivationIndicator() {
    ConfidenceMotivationIndicator(confidence = 25f, motivation = 75f)
}

@Composable
fun ConfidenceMotivationIndicator(confidence: Float?, motivation: Float?,
                                  modifier: Modifier = Modifier) {
    if(confidence != null && motivation != null) {
        Surface(modifier = modifier, shape = CircleShape, elevation = 1.dp) {
            CircularProgressIndicator(progress = max(confidence / 100f, 0.01f),
                color = MaterialTheme.colors.secondary,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(24.dp))

            CircularProgressIndicator(progress = max(motivation / 100f, 0.01f),
                color = MaterialTheme.colors.secondary,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp))
        }
    }
}