package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
import kotlin.math.max

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ActivityDetailsDialog(
    onDismiss: () -> Unit,
    id: String? = null,
) {
    val model: EditActivityViewModel =
        viewModel("activityDetailsViewModel", EditActivityViewModelFactory())
    model.prepareActivity(id)
    ActivityDetailsDialog(onDismiss = onDismiss, model)
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ActivityDetailsDialog(onDismiss: () -> Unit, model: EditActivityViewModel) {
    Dialog(onDismissRequest = onDismiss) {
        ActivityDetailsDialogContent(onDismiss, model)
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ActivityDetailsDialogContent(onDismiss: () -> Unit, model: EditActivityViewModel) {
    val title by model.title.observeAsState()
    val text by model.text.observeAsState()
    val date by model.date.observeAsState()
    val type by model.type.observeAsState()
    val language by model.language.observeAsState()
    val startTime by model.startTime.observeAsState()
    val endTime by model.endTime.observeAsState()
    val confidence by model.confidence.observeAsState()
    val motivation by model.motivation.observeAsState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { null },
                elevation = 0.dp,
                backgroundColor = Color.Transparent,
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = null)
                    }
                }
            )
        }
    )
    {
        Column(Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())) {
            Text(title.orEmpty(), style = MaterialTheme.typography.h5)
            Spacer(Modifier.size(8.dp))
            Text("${getLanguageDisplayName(language.orEmpty())} · ${type?.name}",
                style = MaterialTheme.typography.caption)
            Spacer(Modifier.size(16.dp))
            applyTextStyle(MaterialTheme.typography.body1, ContentAlpha.medium) {
                Text(text.orEmpty())
            }
            Spacer(Modifier.size(16.dp))
            Divider()
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Category", style = MaterialTheme.typography.body2)
                applyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(type?.category?.title.orEmpty())
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Date", style = MaterialTheme.typography.body2)
                applyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(toDateString(date))
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Start Time", style = MaterialTheme.typography.body2)
                applyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(toTimeString(startTime))
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Duration", style = MaterialTheme.typography.body2)
                applyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(getDurationString(getMinutes(startTime!!, endTime!!)))
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Confidence", style = MaterialTheme.typography.body2)
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = 1f,
                        color = Color.LightGray.copy(alpha = ContentAlpha.disabled),
                        strokeWidth = 4.dp,
                        modifier = Modifier
                            .size(24.dp))
                    CircularProgressIndicator(progress = max(confidence!! / 100f, 0.01f),
                        color = MaterialTheme.colors.primary,
                        strokeWidth = 4.dp,
                        modifier = Modifier
                            .size(24.dp))
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Motivation", style = MaterialTheme.typography.body2)
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = 1f,
                        color = Color.LightGray.copy(alpha = ContentAlpha.disabled),
                        strokeWidth = 4.dp,
                        modifier = Modifier
                            .size(24.dp))
                    CircularProgressIndicator(progress = max(motivation!! / 100f, 0.01f),
                        color = MaterialTheme.colors.secondary,
                        strokeWidth = 4.dp,
                        modifier = Modifier
                            .size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun applyTextStyle(
    textStyle: TextStyle,
    contentAlpha: Float,
    icon: @Composable (() -> Unit)?,
) {
    if (icon != null) {
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
            ProvideTextStyle(textStyle, icon)
        }
    }
}