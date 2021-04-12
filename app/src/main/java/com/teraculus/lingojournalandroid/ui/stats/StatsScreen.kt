package com.teraculus.lingojournalandroid.ui.stats

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun StatsScreen(onItemClick: (id: String) -> Unit) {
    StatsContent(onItemClick = onItemClick)
}