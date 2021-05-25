package com.teraculus.lingojournalandroid.ui.goals

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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.GoalType
import com.teraculus.lingojournalandroid.ui.components.*
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

    Scaffold(
        topBar = {
            val elevation =
                if (MaterialTheme.colors.isLight && (scrollState.value > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
            TopAppBar(
                title = { Text(text = "New Goal") },
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation,
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
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showActivityTypeDialog by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

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
        Spacer(modifier = Modifier.size(16.dp))
        Label(text = "Goal type")
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