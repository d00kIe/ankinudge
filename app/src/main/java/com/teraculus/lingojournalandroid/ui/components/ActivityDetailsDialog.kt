package com.teraculus.lingojournalandroid.ui.components

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
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.MeasurementUnit
import com.teraculus.lingojournalandroid.utils.*
import com.teraculus.lingojournalandroid.viewmodel.ActivityDetailsViewModel

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ActivityDetailsDialogContent(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    model: ActivityDetailsViewModel,
) {
    val title by model.title.observeAsState()
    val text by model.text.observeAsState()
    val type by model.type.observeAsState()
    val typeName by model.typeName.observeAsState()
    val categoryTitle by model.categoryTitle.observeAsState()
    val date by model.date.observeAsState()
    val startTime by model.startTime.observeAsState()
    val endTime by model.endTime.observeAsState()
    val unitCount by model.unitCount.observeAsState()
    val confidence by model.confidence.observeAsState()
    val motivation by model.motivation.observeAsState()
    val language by model.language.observeAsState()

    var expandedMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            val elevation =
                if (MaterialTheme.colors.isLight && (scrollState.value > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
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
            .padding(vertical = 8.dp, horizontal = 24.dp)
            .verticalScroll(scrollState)) {
            Text(if (title.isNullOrEmpty()) toActivityTypeTitle(type) else title.orEmpty(),
                style = MaterialTheme.typography.h5)
            Spacer(Modifier.size(8.dp))
            ApplyTextStyle(MaterialTheme.typography.body2, ContentAlpha.medium) {
                Text("${getLanguageDisplayName(language.orEmpty())} · $typeName")
            }
            if (!text.isNullOrEmpty()) {
                Spacer(Modifier.size(16.dp))
                ApplyTextStyle(MaterialTheme.typography.body1, ContentAlpha.medium) {
                    Text(text.orEmpty())
                }
            }
            Spacer(Modifier.size(16.dp))
            Divider()
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Category", style = MaterialTheme.typography.body2)
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(categoryTitle.orEmpty())
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Date", style = MaterialTheme.typography.body2)
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(toDateString(date))
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Start Time", style = MaterialTheme.typography.body2)
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(toTimeString(startTime))
                }
            }

            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(MeasurementUnit.Time.title, style = MaterialTheme.typography.body2)
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text(getDurationString(getMinutes(startTime, endTime)))
                }
            }

            type?.unit?.let { unit ->
                Spacer(Modifier.size(16.dp))
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(unit.title, style = MaterialTheme.typography.body2)
                    ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                        Text(getMeasurementUnitValueString(unit, unitCount ?: 0f))
                    }
                }
            }

            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Confidence", style = MaterialTheme.typography.body2)
                SentimentIcon(value = confidence)
            }
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Motivation", style = MaterialTheme.typography.body2)
                SentimentIcon(value = motivation)
            }
            Spacer(Modifier.size(32.dp))
        }
    }
}