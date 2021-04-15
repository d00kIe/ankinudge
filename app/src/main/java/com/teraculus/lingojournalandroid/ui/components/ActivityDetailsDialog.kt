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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.utils.*
import com.teraculus.lingojournalandroid.viewmodel.ActivityDetailsViewModel
import com.teraculus.lingojournalandroid.viewmodel.ActivityDetailsViewModelFactory
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModelFactory
import kotlin.math.max

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ActivityDetailsDialogContent(onDismiss: () -> Unit,
                                 onDelete: () -> Unit,
                                 onEdit: () -> Unit,
                                 model : ActivityDetailsViewModel) {
    val title by model.title.observeAsState()
    val text by model.text.observeAsState()
    val typeName by model.typeName.observeAsState()
    val categoryTitle by model.categoryTitle.observeAsState()
    val date by model.date.observeAsState()
    val startTime by model.startTime.observeAsState()
    val endTime by model.endTime.observeAsState()
    val confidence by model.confidence.observeAsState()
    val motivation by model.motivation.observeAsState()
    val language by model.language.observeAsState()

    var expandedMenu by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            val elevation = if(!MaterialTheme.colors.isLight) 0.dp else AppBarDefaults.TopAppBarElevation
            TopAppBar(
                title = { },
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation,
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit() }) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                    }
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(onClick = { onDelete(); model.delete(); }) {
                            Text("Delete")
                        }
                    }
                }
            )
        }
    )
    {
        Column(Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 24.dp)
            .verticalScroll(rememberScrollState())) {
            Text(title.orEmpty(), style = MaterialTheme.typography.h5)
            Spacer(Modifier.size(8.dp))
            ApplyTextStyle(MaterialTheme.typography.body2, ContentAlpha.medium) {
                Text("${getLanguageDisplayName(language.orEmpty())} · $typeName")
            }
            if(!text.isNullOrEmpty()) {
                Spacer(Modifier.size(16.dp))
                ApplyTextStyle(MaterialTheme.typography.body1, ContentAlpha.medium) {
                    Text(text.orEmpty())
                }
            }
            Spacer(Modifier.size(16.dp))
            Divider()
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Category", style = MaterialTheme.typography.body2)
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(categoryTitle.orEmpty())
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Date", style = MaterialTheme.typography.body2)
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(toDateString(date))
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Start Time", style = MaterialTheme.typography.body2)
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(toTimeString(startTime))
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Duration", style = MaterialTheme.typography.body2)
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(getDurationString(getMinutes(startTime, endTime)))
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Confidence", style = MaterialTheme.typography.body2)
                SentimentIcon(value = confidence)
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Motivation", style = MaterialTheme.typography.body2)
                SentimentIcon(value = motivation)
            }
            Spacer(Modifier.size(32.dp))
        }
    }
}