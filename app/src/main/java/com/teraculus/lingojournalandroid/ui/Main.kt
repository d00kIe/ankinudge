package com.teraculus.lingojournalandroid.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.R
import com.teraculus.lingojournalandroid.viewmodel.MainViewModel

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MainContent(
    onOpenSettings: () -> Unit,
    viewModel: MainViewModel = viewModel(key = "mainViewModel"),
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
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Rounded.Settings, contentDescription = null)
                    }
                })
        },
        floatingActionButton = {
            FAB {/*todo click*/}
        }
    ) {
        if (newUser == true) {
            WelcomingScreen()
        } else {

        }
    }
}

@Composable
private fun WelcomingScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 128.dp),
        contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(id = R.drawable.ic_welcome_icon),
                contentDescription = null,
                modifier = Modifier.size(142.dp))
            Spacer(modifier = Modifier.size(16.dp))
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