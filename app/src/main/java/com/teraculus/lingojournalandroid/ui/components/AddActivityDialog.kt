package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.utils.getDurationString
import com.teraculus.lingojournalandroid.utils.getMinutes
import com.teraculus.lingojournalandroid.utils.toDateString
import com.teraculus.lingojournalandroid.utils.toTimeString
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModelFactory
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun AddActivityDialogContent(onDismiss: () -> Unit, model: EditActivityViewModel) {
    val title = model.title.observeAsState()
    val text = model.text.observeAsState()
    val date = model.date.observeAsState()
    val type by model.type.observeAsState()
    val language = model.language.observeAsState()
    val startTime = model.startTime.observeAsState()
    val endTime = model.endTime.observeAsState()
    val confidence by model.confidence.observeAsState()
    val motivation by model.motivation.observeAsState()
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showActivityTypeDialog by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    if(showLanguageDialog) {
        LanguageSelectDialog(
            onItemClick = {
                            model.onLanguageChange(it.code)
                            showLanguageDialog = false
                          },
            onDismissRequest = { showLanguageDialog = false })
    }

    if(showActivityTypeDialog) {
        ActivityTypeSelectDialog(
            onItemClick = {
                model.onTypeChange(it)
                showActivityTypeDialog = false
            },
            onDismissRequest = { showActivityTypeDialog = false })
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
                    value = getLanguageDisplayName(language.value.orEmpty()) ,
                    onClick = { showLanguageDialog = true })
            }
            Row {
                DropDownTextField(label = { Text("Activity") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    leadingIcon = { type?.category?.icon?.let { it1 -> painterResource(id = it1) }?.let { it2 -> Icon(painter = it2, modifier = Modifier.size(24.dp), contentDescription = null) } },
                    value = "${type?.category?.title} : ${type?.name}",
                    onClick = { showActivityTypeDialog = true })
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
                getDurationString(getMinutes(startTime.value!!, endTime.value!!)),
                { coroutineScope.launch { model.pickDate() } },
                { coroutineScope.launch { model.pickStartTime() } },
                { coroutineScope.launch { model.pickDuration() } })

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
    duration: String,
    onPickDate: () -> Unit,
    onPickStartTime: () -> Unit,
    onPickDuration: () -> Unit,
) {
    Column(Modifier
        .fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
            DropDownTextField(
                label = { Text("Date") },
                value = toDateString(date),
                modifier = Modifier
                    .weight(0.5f)
                    .padding(end = 8.dp),
                onClick = onPickDate
            )
            DropDownTextField(
                label = { Text("Start Time") },
                value = toTimeString(startTime),
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = 8.dp),
                onClick = onPickStartTime
            )
        }
        Row(Modifier
            .padding(16.dp)) {
            DropDownTextField(
                label = { Text("Duration") },
                value = duration,
                modifier = Modifier
                    .weight(0.5f),
                onClick = onPickDuration
            )
        }
    }
}