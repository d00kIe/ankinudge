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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.teraculus.lingojournalandroid.data.Language
import com.teraculus.lingojournalandroid.utils.toDateString
import com.teraculus.lingojournalandroid.utils.toTimeString
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModelFactory
import java.time.LocalDate
import java.time.LocalTime

@ExperimentalMaterialApi
@Composable
fun AddActivityDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    id: String? = null,
) {
    val model: EditActivityViewModel =
        viewModel("editActivityViewModel", EditActivityViewModelFactory())
    model.prepareActivity(id)
    AddActivityDialog(show = show, onDismiss = onDismiss, model)
}

@ExperimentalMaterialApi
@Composable
fun AddActivityDialog(show: Boolean, onDismiss: () -> Unit, model: EditActivityViewModel) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            AddActivityDialogContent(onDismiss, model)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AddActivityDialogContent(onDismiss: () -> Unit, model: EditActivityViewModel) {
    val title = model.title.observeAsState()
    val text = model.text.observeAsState()
    val date = model.date.observeAsState()
    val language = model.language.observeAsState()
    val startTime = model.startTime.observeAsState()
    val endTime = model.endTime.observeAsState()
    val confidence by model.confidence.observeAsState()
    val motivation by model.motivation.observeAsState()
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    if(showLanguageDialog) {
        LanguageSelectDialog(
            onItemClick = {
                            model.onLanguageChange((it as Language).code)
                            showLanguageDialog = false
                          },
            onDismissRequest = { showLanguageDialog = false })
    }

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
                DropDownTextField(label = { Text("Language") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = language.value.orEmpty() ,
                    onClick = { showLanguageDialog = true })
            }
            Row {
                DropDownTextField(label = { Text("Activity") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = "",
                    onClick = { /*TODO*/ })
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

            Row(Modifier.padding(bottom = 8.dp)) {
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

@Composable
private fun DateAndTimeRow(
    date: LocalDate?,
    startTime: LocalTime?,
    endTime: LocalTime?,
    onPickDate: () -> Unit,
    onPickStartTime: () -> Unit,
    onPickEndTime: () -> Unit,
) {
    Column(Modifier
        .fillMaxSize()) {
        Row(Modifier
            .padding(16.dp)) {
            DropDownTextField(
                label = { Text("Date") },
                value = toDateString(date),
                modifier = Modifier
                    .weight(0.5f),
                onClick = onPickDate
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)) {
            DropDownTextField(
                label = { Text("Start Time") },
                value = toTimeString(startTime),
                modifier = Modifier
                    .weight(0.5f)
                    .padding(end = 8.dp),
                onClick = onPickStartTime
            )
            DropDownTextField(
                label = { Text("End Time") },
                value = toTimeString(endTime),
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = 8.dp),
                onClick = onPickEndTime
            )
        }
    }
}