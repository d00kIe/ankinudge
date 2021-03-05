package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.utils.toDateString
import com.teraculus.lingojournalandroid.utils.toTimeString
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModelFactory

@Composable
fun AddActivityDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    id: String? = null,
    pickerProvider: PickerProvider,
) {
    val model: EditActivityViewModel =
        viewModel("editActivityViewModel", EditActivityViewModelFactory(pickerProvider))
    model.prepareActivity(id)
    AddActivityDialog(show = show, onDismiss = onDismiss, model)
}

@Composable
fun AddActivityDialog(show: Boolean, onDismiss: () -> Unit, model: EditActivityViewModel) {
    if (show) {
        val title = model.title.observeAsState()
        val text = model.text.observeAsState()
        val date = model.date.observeAsState()
        val startTime = model.startTime.observeAsState()
        val endTime = model.endTime.observeAsState()
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
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())) {
                    Row {
                        OutlinedTextField(label = { Text("Language") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            value = "",
                            onValueChange = { /*TODO*/ })
                    }
                    Row {
                        OutlinedTextField(label = { Text("Activity") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    Divider(Modifier.padding(top = 16.dp))
                    Column {
                        Row(Modifier
                            .padding(16.dp)
                            .clickable { model.pickDate() },
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Event, contentDescription = null)
                            Text(text = toDateString(date.value), modifier = Modifier
                                .padding(horizontal = 8.dp))
                        }
                        Row(Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Row(Modifier
                                .weight(1f)
                                .clickable { model.pickStartTime() }) {
                                Icon(Icons.Filled.Schedule, contentDescription = null)
                                Text(text = toTimeString(startTime.value),
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp))

                            }
                            Row(Modifier
                                .weight(1f)
                                .clickable { model.pickEndTime() }) {
                                Icon(Icons.Filled.Timelapse, contentDescription = null)
                                Text(text = toTimeString(endTime.value),
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp))
                            }
                        }
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
                    Row(Modifier.padding(bottom = 16.dp)) {
                        Text("Confidence", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
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
                    Row(Modifier.padding(bottom = 16.dp)) {
                        Text("Motivation", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}