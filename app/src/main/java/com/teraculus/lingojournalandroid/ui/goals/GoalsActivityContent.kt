package com.teraculus.lingojournalandroid.ui.goals

import android.util.Range
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.TaskAlt
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
import com.teraculus.lingojournalandroid.model.GoalType
import com.teraculus.lingojournalandroid.ui.components.Label
import com.teraculus.lingojournalandroid.utils.getDurationString
import com.teraculus.lingojournalandroid.utils.toActivityTypeTitle
import com.teraculus.lingojournalandroid.viewmodel.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
        val progress = model.progress.observeAsState()
        val progressPercent = model.progressPercent.observeAsState()
        val context = LocalContext.current

        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        SecondaryText(goal, progress.value, progressPercent.value)
                    },
                    trailing= {
                        IconButton(onClick = { model.edit(context) }) {
                            Icon(Icons.Rounded.Edit, contentDescription = null)
                        }
                    }
                )
                GoalChart(goal)
            }
        }
    }
}

@Composable
private fun SecondaryText(
    goal: ActivityGoal,
    progress: Float?,
    progressPercent: Float?
) {
    val type = remember(goal) { goal.activityType }
    val language = remember(goal) { goal.language }

    val progressInt = (progress?:0f).toInt()
    val durationGoal = goal.durationGoal ?: 0
    val unitCountGoal = (goal.unitCountGoal ?: 0f).toInt()
    val progressString = if(goal.effortUnit == EffortUnit.Time) "${getDurationString( durationGoal - progressInt)} left" else "${progressInt}/${unitCountGoal} ${type?.unit?.unitSuffix}"
    val values = listOf(
        "${(progressPercent?:0f).toInt()}%",
        progressString,
        getLanguageDisplayName(language))

    Column {
        Text(modifier = Modifier.padding(bottom = 8.dp),
            text = values.filterNotNull().joinToString(separator = " · "),
            style = MaterialTheme.typography.body2)
    }
}


@Composable
private fun GoalChart(
    goal: ActivityGoal
) {
    val goalType = remember(goal) { goal.type }
    val cardColor = remember(goal) { goal.activityType?.category?.color?.let { it1 -> Color(it1) }  }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        when (goalType) {
            GoalType.Daily -> {
                val model: RangeGoalProgressViewModel = viewModel("dailyViewModel${goal.id}", RangeGoalProgressViewModel.Factory(
                    Range.create(LocalDate.now().minusDays(7), LocalDate.now()), goal.id.toString()))
                val perDayMap by model.perDayGoals.observeAsState()
                val firstDate = remember { LocalDate.now().minusDays(6) }
                val values = remember(perDayMap) { perDayMap.orEmpty().mapKeys { ChronoUnit.DAYS.between(firstDate, it.key).toInt() } }
                Label(text = "Last 7 days", modifier = Modifier.padding(top = 8.dp))
                ProgressBarChart(
                    color = cardColor ?: MaterialTheme.colors.primary,
                    firstDate = firstDate,
                    values = values)
            }
            GoalType.LongTerm -> {
                val firstDate = remember { LocalDate.now().minusDays(29) }
                val range = remember {
                    Range.create(firstDate, LocalDate.now())
                }
                val model: AccumulatingRangeGoalProgressViewModel = viewModel("accumulatingDailyViewModel${goal.id}", AccumulatingRangeGoalProgressViewModel.Factory(
                    range, goal.id.toString()))
                val perDayMap by model.perDayGoals.observeAsState()
                val values = remember(perDayMap) { perDayMap.orEmpty().mapKeys { ChronoUnit.DAYS.between(firstDate, it.key).toInt() } }
                Label(text = "Last 30 days", modifier = Modifier.padding(top = 8.dp))
                ProgressLineChart(
                    color = cardColor ?: MaterialTheme.colors.primary,
                    firstDate = firstDate,
                    dayCount = 30,
                    values = values
                )
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
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.TaskAlt,
                contentDescription = null,
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier.size(142.dp))
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "Set goals!",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Setting goals gives your learning direction, boosts your motivation and self-confidence. Click on the green button to create your first goal!",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
        }
    }
}