package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.utils.toDateString
import com.teraculus.lingojournalandroid.utils.toTimeString
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModelFactory
import java.time.LocalDate
import java.time.LocalTime

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
        Dialog(onDismissRequest = onDismiss) {
            AddActivityDialogContent(onDismiss, model)
        }
    }
}

@Composable
fun AddActivityDialogContent(onDismiss: () -> Unit, model: EditActivityViewModel) {
    val title = model.title.observeAsState()
    val text = model.text.observeAsState()
    val date = model.date.observeAsState()
    val startTime = model.startTime.observeAsState()
    val endTime = model.endTime.observeAsState()
    val confidence by model.confidence.observeAsState()
    val motivation by model.motivation.observeAsState()

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
                    TextButton(onClick = { model.save(); onDismiss(); }) {
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

            DateAndTimeRow(date.value,
                startTime.value,
                endTime.value,
                { model.pickDate() },
                { model.pickStartTime() },
                { model.pickEndTime() })

            Divider()
            Row(Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                Text("Confidence",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle2)
            }
            SentimentIcons(value = confidence,
                onSentimentChange = { model.onConfidenceChange(it) },
                color = MaterialTheme.colors.primary,
                size = 36.dp)
            Row(Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                Text("Motivation",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle2)
            }
            SentimentIcons(value = motivation,
                onSentimentChange = { model.onMotivationChange(it) },
                color = MaterialTheme.colors.secondary,
                size = 36.dp)
        }
    }
}

@Preview(name = "Date And time")
@Composable
private fun DateAndTimeRow() {
    val date = LocalDate.of(2020, 2, 25)
    val startTime = LocalTime.of(12, 44)
    val endTime = LocalTime.of(13, 44)
    DateAndTimeRow(date, startTime, endTime, {}, {}, {})
}

@Composable
private fun DateAndTimeRow(
    date: LocalDate?,
    startTime: LocalTime?,
    endTime: LocalTime?,
    onPickDate: () -> Unit,
    onPickStartTime: () -> Unit,
    onPickEndTime: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(Modifier
        .fillMaxSize()) {
        Row(Modifier
            .padding(16.dp)) {
            OutlinedTextField(
                label = { Text("Date") },
                readOnly = true,
                value = toDateString(date),
                onValueChange = {},
                modifier = Modifier
                    .weight(0.5f)
                    .onFocusChanged(
                        onFocusChanged = {
                            if (it == FocusState.Active) {
                                onPickDate()
                                focusManager.clearFocus()
                            }
                        },
                    )
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)) {
            OutlinedTextField(
                label = { Text("Start Time") },
                readOnly = true,
                value = toTimeString(startTime),
                onValueChange = {},
                modifier = Modifier
                    .weight(0.5f)
                    .padding(end = 8.dp)
                    .onFocusChanged(
                        onFocusChanged = {
                            if (it == FocusState.Active) {
                                onPickStartTime()
                                focusManager.clearFocus()
                            }
                        },
                    )
            )
            OutlinedTextField(
                label = { Text("End Time") },
                readOnly = true,
                value = toTimeString(endTime),
                onValueChange = {},
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = 8.dp)
                    .onFocusChanged(
                        onFocusChanged = {
                            if (it == FocusState.Active) {
                                onPickEndTime()
                                focusManager.clearFocus()
                            }
                        },
                    )
            )
        }
    }
}