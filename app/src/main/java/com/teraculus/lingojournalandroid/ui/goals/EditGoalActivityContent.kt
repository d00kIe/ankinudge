package com.teraculus.lingojournalandroid.ui.goals

import android.util.Range
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.EffortUnit
import com.teraculus.lingojournalandroid.model.GoalType
import com.teraculus.lingojournalandroid.model.MeasurementUnit
import com.teraculus.lingojournalandroid.ui.components.*
import com.teraculus.lingojournalandroid.utils.getDurationString
import com.teraculus.lingojournalandroid.utils.toActivityTypeTitle
import com.teraculus.lingojournalandroid.utils.toDateString
import com.teraculus.lingojournalandroid.viewmodel.EditGoalViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditGoalViewModelFactory
import java.time.DayOfWeek

@Composable
fun AddGoalActivityContent(
    goalId: String?,
    onDismiss: () -> Unit,
    model: EditGoalViewModel = viewModel("addGoalViewModel",
        EditGoalViewModelFactory(goalId)),
) {
    val scrollState = rememberScrollState()
    var expandedMenu by remember { mutableStateOf(false)}
    val active by model.active.observeAsState()

    Scaffold(
        topBar = {
            val elevation =
                if (MaterialTheme.colors.isLight && (scrollState.value > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
            val focusManager = LocalFocusManager.current
            TopAppBar(
                title = { Text(text = if(goalId.isNullOrEmpty()) "New goal" else "Edit goal") },
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation,
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    // RowScope here, so these icons will be placed horizontally
                    TextButton(onClick = { focusManager.clearFocus(); model.save(); onDismiss(); }) {
                        Text(text = "Save")
                    }
                    if(!goalId.isNullOrEmpty()) {
                        IconButton(onClick = { expandedMenu = true }) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(onClick = { model.delete(); onDismiss(); }) {
                                Text("Delete")
                            }
                            if(active == true) {
                                DropdownMenuItem(onClick = { model.archive(); onDismiss(); }) {
                                    Text("Archive")
                                }
                            }
                        }
                    }
                }
            )
        }
    )
    {
        AddGoalFields(model, scrollState)
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun AddGoalFields(model: EditGoalViewModel, scrollState: ScrollState) {

    val typeGroups = model.groupedTypes.observeAsState()
    val preferences by model.preferences.observeAsState()
    val language by model.language.observeAsState()
    val activityType by model.activityType.observeAsState()
    val type by model.type.observeAsState()
    val weekDays by model.weekDays.observeAsState()
    val startDate by model.startDate.observeAsState()
    val endDate by model.endDate.observeAsState()
    val effortUnit by model.effortUnit.observeAsState()
    val durationGoal by model.durationGoal.observeAsState()
    val hoursGoal by model.hoursGoal.observeAsState()
    val minutesGoal by model.minutesGoal.observeAsState()
    val unitCountGoal by model.unitCountGoal.observeAsState()
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showDurationPicker by rememberSaveable { mutableStateOf(false) }
    var showActivityTypeDialog by rememberSaveable { mutableStateOf(false) }


    if (showLanguageDialog) {
        LanguageSelectDialog(
            onItemClick = {
                model.setLanguage(it.code)
                showLanguageDialog = false
            },
            onDismissRequest = { showLanguageDialog = false },
            preferences = preferences)
    }

    if (showActivityTypeDialog) {
        ActivityTypeSelectDialog(
            groups = typeGroups,
            onItemClick = {
                model.setActivityType(it)
                showActivityTypeDialog = false
            },
            onAddTypeClick = {
                model.addActivityType(it)
            },
            onDismissRequest = { showActivityTypeDialog = false })
    }


    if(showDurationPicker) {
        DurationPicker(onDismissRequest = { showDurationPicker = false },
            hours = hoursGoal,
            minutes = minutesGoal,
            onChange = { h, m ->
                model.setDurationGoal(h, m)
                showDurationPicker = false
            })
    }

    Column(Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp)
        .verticalScroll(scrollState)) {
        Spacer(modifier = Modifier.size(8.dp))
        DropDownTextField(label = { Text("Language") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = getLanguageDisplayName(language.orEmpty()),
            onClick = { showLanguageDialog = true })
        Spacer(modifier = Modifier.size(16.dp))
        DropDownTextField(label = { Text("Activity") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            leadingIcon = { ActivityTypeIcon(activityType?.category) },
            value = toActivityTypeTitle(activityType),
            onClick = { showActivityTypeDialog = true })

        Spacer(modifier = Modifier.size(24.dp))
        Text(text = "Goal type", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.subtitle2)
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Icon(Icons.Rounded.Info, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text(text = "Long-term goals are useful when you need to track your progress over a long time. Like reading a book or a language course.", style = MaterialTheme.typography.caption)
        }
        Spacer(modifier = Modifier.size(8.dp))
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            ToggleButton(onClick = { model.setGoalType(GoalType.Daily) },
                selected = type == GoalType.Daily,
                modifier = Modifier.padding(8.dp),
                highlighted = true) {
                Text("Daily")
            }
            ToggleButton(onClick = { model.setGoalType(GoalType.LongTerm) },
                selected = type == GoalType.LongTerm,
                modifier = Modifier.padding(8.dp),
                highlighted = true) {
                Text("Long-term")
            }
        }

        weekDays?.let { days ->
            AnimatedVisibility(visible = type == GoalType.Daily) {
                Column() {
                    Spacer(modifier = Modifier.size(16.dp))
                    Label(text = "Week days")
                    Spacer(modifier = Modifier.size(8.dp))
                    WeekDaysSelector(weekDays = days, onSelect = { model.toggleWeekDay(it) })
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }

        AnimatedVisibility(visible = type == GoalType.LongTerm) {
            Column() {
                Spacer(modifier = Modifier.size(16.dp))
                DropDownTextField(label = { Text("Start date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    leadingIcon = { Icon(Icons.Rounded.CalendarToday, contentDescription = null) },
                    value = toDateString(startDate),
                    onClick = { model.pickStartDate() })

                Spacer(modifier = Modifier.size(16.dp))
                DropDownTextField(label = { Text("Due by") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    leadingIcon = { Icon(Icons.Rounded.EventAvailable, contentDescription = null) },
                    value = toDateString(endDate),
                    onClick = { model.pickEndDate() })
            }
        }

        Spacer(modifier = Modifier.size(24.dp))
        Text(text = "Effort goal", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.subtitle2)
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        activityType?.unit?.let { unit ->
            AnimatedVisibility(visible = unit != MeasurementUnit.Time ) {
                Column {
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Icon(Icons.Rounded.Info, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text(text = "You can track your progress either by the time you spend or using the specific activity unit. Like pages to read, or course sessions to take.", style = MaterialTheme.typography.caption)
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                        ToggleButton(onClick = { model.setEffortUnit(EffortUnit.Time) },
                            selected = effortUnit == EffortUnit.Time,
                            modifier = Modifier.padding(8.dp),
                            highlighted = true) {
                            Text("Duration")
                        }
                        ToggleButton(onClick = { model.setEffortUnit(EffortUnit.Unit) },
                            selected = effortUnit == EffortUnit.Unit,
                            modifier = Modifier.padding(8.dp),
                            highlighted = true) {
                            Text(activityType?.unit?.title.toString())
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = effortUnit == EffortUnit.Time) {
            Column() {
                Spacer(modifier = Modifier.size(16.dp))
                DropDownTextField(
                    label = { Text("Duration") },
                    value = getDurationString(durationGoal ?: 0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { showDurationPicker = true }
                )
            }
        }

        AnimatedVisibility(visible = effortUnit == EffortUnit.Unit) {
            Column() {
                Spacer(modifier = Modifier.size(16.dp))
                NumberField(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    value = unitCountGoal?.toInt(),
                    onValueChange = { model.setUnitCountGoal(it.toFloat()) },
                    label = activityType?.unit?.title,
                    range = Range.create(0, 50000))
            }
        }
    }
}

@Composable
fun WeekDaysSelector(weekDays: IntArray, onSelect: (Int) -> Unit) {
    val dayLetters = remember {
        IntRange(1, 7).map { DayOfWeek.of(it).toString().substring(0, 1) }
    }

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth()) {
        for (d in 1 until 8) {
            ToggleButton(onClick = { onSelect(d) },
                selected = weekDays.contains(d),
                highlighted = true,
                round = true) {
                Text(dayLetters[d - 1])
            }
        }
    }
}