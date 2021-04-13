package com.teraculus.lingojournalandroid.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.ui.home.HomeScreen


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun Main(
    onActivityClick: (id: String) -> Unit,
    onOpenEditor: (id: String?) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
) {
    LingoTheme {
        MainContent(
            onAddActivity = { onOpenEditor(null) },
            onActivityClick = onActivityClick,
            onOpenSettings = onOpenSettings,
            onOpenStats = onOpenStats)
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MainContent(
    onAddActivity: () -> Unit,
    onActivityClick: (id: String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
) {
    Scaffold(
        topBar = {
            val elevation = if (! MaterialTheme.colors.isLight ) 0.dp else AppBarDefaults.TopAppBarElevation
            TopAppBar(
                title = {
                    Text("Journal",
                        modifier = Modifier.padding(start = 24.dp),
                        style = MaterialTheme.typography.h6)
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation,
                actions = {
                    IconButton(onClick = onOpenStats) {
                        Icon(Icons.Rounded.BarChart, contentDescription = null)
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Rounded.Settings, contentDescription = null)
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddActivity) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    Modifier.size(32.dp)
                )
            }
        }
    ) {
        HomeScreen(onItemClick = onActivityClick, onOpenStats = onOpenStats)
    }
}