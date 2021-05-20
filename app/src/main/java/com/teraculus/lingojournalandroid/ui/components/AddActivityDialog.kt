package com.teraculus.lingojournalandroid.ui.components

import android.util.Range
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.model.UnitSelector
import com.teraculus.lingojournalandroid.utils.*
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModel
import kotlinx.coroutines.launch

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
    val unitCount by model.unitCount.observeAsState()
    val confidence by model.confidence.observeAsState()
    val motivation by model.motivation.observeAsState()
    val preferences by model.preferences.observeAsState()
    val typeGroups = model.groupedTypes.observeAsState()
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showActivityTypeDialog by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    if (showLanguageDialog) {
        LanguageSelectDialog(
            onItemClick = {
                model.onLanguageChange(it.code)
                showLanguageDialog = false
            },
            onDismissRequest = { showLanguageDialog = false },
            preferences = preferences)
    }

    if (showActivityTypeDialog) {
        ActivityTypeSelectDialog(
            groups = typeGroups,
            onItemClick = {
                model.onTypeChange(it)
                showActivityTypeDialog = false
            },
            onAddTypeClick = {
                model.addActivityType(it)
            },
            onDismissRequest = { showActivityTypeDialog = false })
    }
    Scaffold(
        topBar = {
            val elevation =
                if (MaterialTheme.colors.isLight && (scrollState.value > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
            TopAppBar(
                title = { Text("Activity") },
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation,
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = null)
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
            .padding(horizontal = 8.dp)
            .verticalScroll(scrollState)) {
            Spacer(modifier = Modifier.size(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)),
                    onClick = { coroutineScope.launch { model.pickDate() } }
                ) {
                    Icon(Icons.Rounded.CalendarToday, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(toDateString(date.value))
                }
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)),
                    onClick = { coroutineScope.launch { model.pickStartTime() } }
                ) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(toTimeString(startTime.value))
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = title.value.toString(),
                onValueChange = { model.onTitleChange(it) })
            Spacer(modifier = Modifier.size(16.dp))
            DropDownTextField(label = { Text("Language") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = getLanguageDisplayName(language.value.orEmpty()),
                onClick = { showLanguageDialog = true })
            Spacer(modifier = Modifier.size(16.dp))
            DropDownTextField(label = { Text("Activity") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                leadingIcon = { ActivityTypeIcon(type?.category) },
                value = toActivityTypeTitle(type),
                onClick = { showActivityTypeDialog = true })
            Spacer(modifier = Modifier.size(16.dp))
            Divider()
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(label = { Text("Note") },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = text.value.toString(),
                onValueChange = { model.onTextChange(it) })

            if(type?.unit?.selector == UnitSelector.TimePicker) {
                Spacer(modifier = Modifier.size(16.dp))
                DropDownTextField(
                    label = { Text("Duration") },
                    value = getDurationString(getMinutes(startTime.value, endTime.value)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { coroutineScope.launch { model.pickDuration() } }
                )
            } else if (type?.unit?.selector == UnitSelector.Count) {
                Spacer(modifier = Modifier.size(16.dp))
                NumberField(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    value = unitCount?.toInt(),
                    onValueChange = { value -> value?.let { model.onUnitCountChange(it.toFloat())} },
                    label = type?.unit?.title,
                    range = Range.create(0, 1000000))

            }

            Spacer(modifier = Modifier.size(16.dp))
            ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                Text("Confidence", modifier = Modifier.padding(horizontal = 16.dp))
            }
            Spacer(modifier = Modifier.size(8.dp))
            SentimentIcons(value = confidence,
                onSentimentChange = { model.onConfidenceChange(it) },
                color = MaterialTheme.colors.secondary,
                size = 36.dp)
            Spacer(modifier = Modifier.size(16.dp))
            ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                Text("Motivation", modifier = Modifier.padding(horizontal = 16.dp))
            }
            Spacer(modifier = Modifier.size(8.dp))
            SentimentIcons(value = motivation,
                onSentimentChange = { model.onMotivationChange(it) },
                color = MaterialTheme.colors.secondary,
                size = 36.dp)
        }
    }
}

@Composable
fun NumberField(
    modifier: Modifier = Modifier,
    value: Int?,
    onValueChange: (value: Int?) -> Unit,
    range: Range<Int>?,
    label: String?,
) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Box(modifier = Modifier
            .weight(1f)
            .padding(top = 8.dp), contentAlignment = Alignment.Center) {
            Button(
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .defaultMinSize(48.dp, 48.dp),
                onClick = {
                    val newVal = (value ?: 0) - 1
                    if (range == null || range.contains(newVal)) {
                        onValueChange(newVal)
                    }
                }) {
                Icon(Icons.Rounded.Remove,
                    contentDescription = null)
            }
        }

        OutlinedTextField(value = value?.toString() ?: "",
            label = { Text(label.orEmpty()) },
            placeholder = { Text("0") },
            modifier = Modifier.weight(3.0f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                var f: Int? = null
                if(it.isEmpty()) {
                    onValueChange(f)
                }
                else {
                    f = it.toIntOrNull()
                    if (f != null && value != f && (range == null || range.contains(f))) {
                        onValueChange(f)
                    }
                }
            }
        )
        Box(modifier = Modifier
            .weight(1f)
            .padding(top = 8.dp), contentAlignment = Alignment.Center) {
            Button(
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.defaultMinSize(48.dp, 48.dp),
                onClick = {
                    val newVal = (value ?: 0) + 1
                    if (range == null || range.contains(newVal)) {
                        onValueChange(newVal)
                    }
                }) {
                Icon(Icons.Rounded.Add,
                    contentDescription = null)
            }
        }
    }

}

@Composable
fun ActivityTypeIcon(category: ActivityCategory?) {
    category?.let {
        Surface(elevation = 0.dp,
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = Color(category.color)) {
            Icon(painter = painterResource(id = category.icon),
                modifier = Modifier
                    .size(16.dp)
                    .padding(8.dp),
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = null)
        }
    }
}