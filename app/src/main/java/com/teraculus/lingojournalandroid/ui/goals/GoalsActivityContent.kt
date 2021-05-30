package com.teraculus.lingojournalandroid.ui.goals

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.ActivityGoal
import com.teraculus.lingojournalandroid.model.EffortUnit
import com.teraculus.lingojournalandroid.utils.getDurationString
import com.teraculus.lingojournalandroid.utils.toActivityTypeTitle
import com.teraculus.lingojournalandroid.viewmodel.GoalItemViewModel
import com.teraculus.lingojournalandroid.viewmodel.GoalItemViewModelFactory
import com.teraculus.lingojournalandroid.viewmodel.GoalsActivityViewModel

@Composable
fun GoalsActivityContent(
    model: GoalsActivityViewModel = viewModel("goalsViewModel"),
    onDismiss: () -> Unit,
    onAddNewGoal: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    val goals by model.frozen.observeAsState()

    Scaffold(
        topBar = {
            val elevation =
                if (MaterialTheme.colors.isLight && (scrollState.firstVisibleItemScrollOffset > 0 || scrollState.firstVisibleItemIndex > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
            TopAppBar(
                title = { Text(text = "Goals") },
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation,
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(text = { Text(text = "New Goal") },
                onClick = { onAddNewGoal() },
                icon = {
                    Icon(Icons.Filled.Add, contentDescription = null)
                })
        }
    )
    {
        if (goals.isNullOrEmpty()) {
            WelcomingScreen()
        } else {
            LazyColumn(state = scrollState) {
                items(goals.orEmpty()) { goal ->
                    key(goal.id) {
                        GoalRow(goal)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun GoalRow(
    rawGoal: ActivityGoal,
    model: GoalItemViewModel = viewModel("goalRow${rawGoal.id}",
        GoalItemViewModelFactory(rawGoal, LocalLifecycleOwner.current)),
) {
    val snapshot by model.snapshot.observeAsState()
    snapshot?.let { goal ->
        val cardColor = remember(goal) { goal.activityType?.category?.color?.let { it1 -> Color(it1) } }
        val goalWeekDaysString by model.goalWeekDaysString.observeAsState()
        val progress = model.progress.observeAsState()
        val progressPercent = model.progressPercent.observeAsState()
        val context = LocalContext.current

        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { model.edit(context) },
            elevation = 2.dp,
            shape = RoundedCornerShape(16.dp)
        )
        {
            Column() {
                ListItem(
                    overlineText = { Text("${goal.type.title} goal")
                    },
                    text = {
                        Text(toActivityTypeTitle(goal.activityType),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    },
                    secondaryText = {
                        SecondaryText(goal, goalWeekDaysString, cardColor, progress.value, progressPercent.value)
                    },
                )

                Row(modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically){
                    TextButton(onClick = { model.edit(context) }) {
                        Text(text = "Edit", color = MaterialTheme.colors.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun SecondaryText(
    goal: ActivityGoal,
    goalWeekDaysString: String?,
    cardColor: Color?,
    progress: Float?,
    progressPercent: Float?
) {
    val type = remember(goal) { goal.activityType }
    val language = remember(goal) { goal.language }

    //val dueBy = if(goal.type == GoalType.Daily) goalWeekDaysString.orEmpty() else "Due by ${toDayStringOrToday(goal.endDate)}"
    val progressInt = (progress?:0f).toInt()
    val durationGoal = goal.durationGoal ?: 0
    val unitCountGoal = (goal.unitCountGoal ?: 0f).toInt()
    val progressString = if(goal.effortUnit == EffortUnit.Time) "${getDurationString( durationGoal - progressInt)} left" else "${progressInt}/${unitCountGoal} ${type?.unit?.unitSuffix}"
    val values = listOf(
        "${(progressPercent?:0f).toInt()}%",
        progressString,
        getLanguageDisplayName(language))

    Column() {
        Text(modifier = Modifier.padding(bottom = 8.dp),
            text = values.filterNotNull().joinToString(separator = " · "),
            style = MaterialTheme.typography.body2)
        Surface(shape = RoundedCornerShape(4.dp)) {
            LinearProgressIndicator(
                progress = (progressPercent ?: 0f ) / 100f,
                color = cardColor ?: MaterialTheme.colors.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun FeedGoalRow(
    rawGoal: ActivityGoal,
    model: GoalItemViewModel = viewModel("goalRow${rawGoal.id}",
        GoalItemViewModelFactory(rawGoal, LocalLifecycleOwner.current)),
    onClick: (goalId: String) -> Unit,
) {
    val snapshot by model.snapshot.observeAsState()
    snapshot?.let { goal ->
        val cardColor = remember(goal) { goal.activityType?.category?.color?.let { it1 -> Color(it1) } }
        val goalWeekDaysString by model.goalWeekDaysString.observeAsState()
        val progress = model.progress.observeAsState()
        val progressPercent = model.progressPercent.observeAsState()

        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick(goal.id.toString()) },
            elevation = 2.dp,
            shape = RoundedCornerShape(16.dp)
        )
        {
            Column() {
                ListItem(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    overlineText = { Text("${goal.type.title} goal")
                    },
                    text = {
                        Text(toActivityTypeTitle(goal.activityType),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    },
                    secondaryText = {
                        SecondaryText(goal, goalWeekDaysString, cardColor, progress.value, progressPercent.value)
                    },
                    trailing = {
                        Box(Modifier.fillMaxHeight(), Alignment.Center) {
                            IconButton(onClick = { onClick(goal.id.toString()) }) {
                                Icon(Icons.Rounded.AddCircle, contentDescription = null, tint = cardColor ?: MaterialTheme.colors.onSurface, modifier = Modifier.size(42.dp))
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun WelcomingScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 128.dp),
        contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)) {
            Text(text = "Set daily goals!",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Daily goals help you stay motivated and practice every day. Click on the green button to create your first goal!",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
        }
    }
}