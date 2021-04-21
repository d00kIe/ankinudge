package com.teraculus.lingojournalandroid.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.ui.components.Pulsating
import com.teraculus.lingojournalandroid.ui.home.HomeScreen
import com.teraculus.lingojournalandroid.viewmodel.MainViewModel


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
    viewModel: MainViewModel = viewModel("mainViewModel"),
) {
    val scrollState = rememberLazyListState()
    val newUser by viewModel.newUser.observeAsState()
    Scaffold(
        topBar = {
            val elevation =
                if (MaterialTheme.colors.isLight && (scrollState.firstVisibleItemScrollOffset > 0 || scrollState.firstVisibleItemIndex > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
            TopAppBar(
                title = {
                    Text("Journal", style = MaterialTheme.typography.h6)
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
            if (newUser == true) {
                Pulsating() {
                    FAB(onAddActivity)
                }
            } else {
                FAB(onAddActivity)
            }
        }
    ) {
        if (newUser == true) {
            WelcomingScreen()
        } else {
            HomeScreen(onItemClick = onActivityClick,
                onOpenStats = onOpenStats,
                scrollState = scrollState)
        }
    }
}

@Composable
private fun WelcomingScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 64.dp),
        contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)) {
            Text(text = "Welcome, language learner!",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Add your first activity by pressing the big green button in the bottom right corner.",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Have fun!",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun FAB(onAddActivity: () -> Unit) {
    FloatingActionButton(onClick = onAddActivity) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            Modifier.size(32.dp)
        )
    }
}