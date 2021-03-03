package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.ui.home.ActivityListViewModel
import com.teraculus.lingojournalandroid.ui.home.ActivityListViewModelFactory
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModelFactory

@Preview
@Composable
fun PreviewAddActivityDialog() {
    AddActivityDialog(show = true, onDismiss = { /*TODO*/ },null)
}

@Composable
fun AddActivityDialog(show: Boolean, onDismiss: () -> Unit, id: String? = null) {
    val model: EditActivityViewModel = viewModel("editActivityViewModel", EditActivityViewModelFactory())
    model.prepareActivity(id)
    AddActivityDialog(show = show, onDismiss = onDismiss, model)
}

@Composable
fun AddActivityDialog(show: Boolean, onDismiss: () -> Unit, model: EditActivityViewModel) {
    if (show) {
        val title = model.title.observeAsState()
        val text = model.text.observeAsState()
        val confidence = model.confidence.observeAsState()
        val motivation = model.motivation.observeAsState()

        Dialog(onDismissRequest = onDismiss) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Activity") },
                        elevation = 0.dp,
                        backgroundColor = Color.Transparent,
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = null)
                            }
                        },
                        actions = {
                            // RowScope here, so these icons will be placed horizontally
                            Button(onClick = { /* doSomething() */ }) {
                                Text(text = "Save")
                            }

                        }
                    )
                }
            )
            {
                Column(Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                    Row {
                        OutlinedTextField(label = { Text("Language") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            value = "",
                            onValueChange = { /*TODO*/ })
                    }
                    Row {
                        OutlinedTextField(label = { Text("Date") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            value = "",
                            onValueChange = { /*TODO*/ })
                    }
                    Row {
                        OutlinedTextField(label = { Text("Start time") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                            value = "",
                            onValueChange = { /*TODO*/ })
                        OutlinedTextField(label = { Text("End time") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                            value = "",
                            onValueChange = { /*TODO*/ })
                    }
                    Row {
                        OutlinedTextField(label = { Text("Title") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            value = title.value.toString(),
                            onValueChange = { model.onTitleChange(it) })
                    }
                    Row {
                        OutlinedTextField(label = { Text("Note") },
                            maxLines = 3,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            value = text.value.toString(),
                            onValueChange = { model.onTextChange(it) })
                    }
                    Row {
                        Divider()
                    }
                    Row {
                        Slider(
                            steps = 5,
                            valueRange = 0f..100f,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            value = confidence.value!!.toFloat(),
                            onValueChange = { model.onConfidenceChange(it) })
                    }
                    Row {
                        Slider(
                            steps = 5,
                            valueRange = 0f..100f,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            value = motivation.value!!.toFloat(),
                            onValueChange = { model.onMotivationChange(it) })
                    }
                }
            }
        }
    }
}