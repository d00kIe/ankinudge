package com.teraculus.lingojournalandroid.ui.goals

import android.util.Range
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.teraculus.lingojournalandroid.utils.*
import com.teraculus.lingojournalandroid.viewmodel.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GoalsActivityContent(
    model: GoalsActivityViewModel = viewModel(key = "goalsViewModel"),
    onDismiss: () -> Unit,
    onAddNewGoal: () -> Unit,
) {
    val activeScrollState = rememberLazyListState()
    val inactiveScrollState = rememberLazyListState()
    val activeGoals by model.activeGoals.observeAsState()
    val inactiveGoals by model.inactiveGoals.observeAsState()
    var tabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            val scrolled =
                if (tabIndex == 0) (activeScrollState.firstVisibleItemScrollOffset > 0 || activeScrollState.firstVisibleItemIndex > 0)
                else (inactiveScrollState.firstVisibleItemScrollOffset > 0 || inactiveScrollState.firstVisibleItemIndex > 0)
            val elevation =
                if (MaterialTheme.colors.isLight && scrolled) AppBarDefaults.TopAppBarElevation else 0.dp
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
                TabRow(
                    selectedTabIndex = tabIndex,
                    backgroundColor = Color.Transparent,
                    divider = {},
                    modifier = Modifier.padding(end = 48.dp)
                ) {
                    Tab(
                        text = { Text("Goals") },
                        selected = 0 == tabIndex,
                        onClick = { tabIndex = 0 }
                    )
                    Tab(
                        text = { Text("Archive") },
                        selected = 1 == tabIndex,
                        onClick = { tabIndex = 1 }
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(tabIndex == 0, exit = fadeOut(), enter = fadeIn()) {
                ExtendedFloatingActionButton(text = { Text(text = "New Goal") },
                    onClick = { onAddNewGoal() },
                    icon = {
                        Icon(Icons.Filled.Add, contentDescription = null)
                    })
            }
        }
    )
    {
        AnimatedVisibility(visible = tabIndex == 0, exit = fadeOut(), enter = fadeIn()) {
            if (activeGoals.isNullOrEmpty()) {
                WelcomingScreen()
            }
            LazyColumn(state = activeScrollState) {
                items(activeGoals.orEmpty()) { goal ->
                    key(goal.id) {
                        GoalRow(goal)
                    }
                }
            }
        }

        AnimatedVisibility(visible = tabIndex == 1, exit = fadeOut(), enter = fadeIn()) {
            if (inactiveGoals.isNullOrEmpty()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Rounded.Info,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp))
                    Text(text = "To archive a goal, open the goal for edit and choose 'Archive' from the menu in top right corner.")
                }
            }
            LazyColumn(state = inactiveScrollState) {
                items(inactiveGoals.orEmpty()) { goal ->
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
    model: GoalItemViewModel = viewModel(key = "goalRow${rawGoal.id}",
        factory = GoalItemViewModelFactory(rawGoal, LocalLifecycleOwner.current)),
) {
    val snapshot by model.snapshot.observeAsState()
    snapshot?.let { goal ->
        val progress by model.progress.observeAsState()
        val progressPercent by model.progressPercent.observeAsState()
        val context = LocalContext.current

        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = 2.dp,
            shape = RoundedCornerShape(16.dp)
        )
        {
            Column() {
                val modifier = if(goal.active) Modifier.clickable { model.edit(context) } else Modifier
                ListItem(
                    modifier = modifier,
                    overlineText = {
                        Text("${goal.type.title} goal · ${toActivityTypeCategoryName(goal.activityType)}")
                    },
                    text = {
                        Text(toShortActivityTypeTitle(goal.activityType),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    },
                    secondaryText = {
                        SecondaryText(goal)
                    },
                    trailing = {
                        if (goal.active) {
                            IconButton(onClick = { model.edit(context) }) {
                                Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = null)
                            }
                        } else {
                            IconButton({ model.delete() }) {
                                Icon(Icons.Rounded.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.error)
                            }
                        }
                    }
                )
                GoalChart(goal, progress, progressPercent)
            }
        }
    }
}

@Composable
private fun SecondaryText(
    goal: ActivityGoal
) {
    val type = remember(goal) { goal.activityType }
    val durationGoal = goal.durationGoal ?: 0
    val unitCountGoal = (goal.unitCountGoal ?: 0f).toInt()
    val goalString =
        if (goal.effortUnit == EffortUnit.Time) "Goal: ${getDurationString(durationGoal)}" else "Goal: $unitCountGoal ${type?.unit?.unitSuffix}"

    val language = remember(goal) { goal.language }
    val values = listOf(
        goalString,
        getLanguageDisplayName(language))
    Text(text = values.joinToString(separator = " · "))
}

@Composable
private fun ChartLabel(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))
}

@Composable
private fun ProgressLabel(
    goal: ActivityGoal,
    progress: Float?,
    progressPercent: Float?,
) {
    val date = if(goal.active) LocalDate.now() else asLocalDate(goal.lastActiveChange)
    val values = listOf(
        toDayStringOrToday(date),
        "${(progressPercent ?: 0f).toInt()}%",)

    ChartLabel(text = values.joinToString(separator = ": "))
}


@Composable
private fun GoalChart(
    goal: ActivityGoal,
    progress: Float?,
    progressPercent: Float?,
) {
    val goalType = remember(goal) { goal.type }
    val cardColor = remember(goal) { goal.activityType?.category?.color?.let { it1 -> Color(it1) } }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        when (goalType) {
            GoalType.Daily -> {
                val model: RangeGoalProgressViewModel =
                    viewModel(key = "dailyViewModel${goal.id}", factory = RangeGoalProgressViewModel.Factory(
                        Range.create(LocalDate.now().minusDays(7), LocalDate.now()),
                        goal.id.toString()))
                val perDayMap by model.perDayGoals.observeAsState()
                val firstDate = remember { LocalDate.now().minusDays(6) }
                val values = remember(perDayMap) {
                    perDayMap.orEmpty()
                        .mapKeys { ChronoUnit.DAYS.between(firstDate, it.key).toInt() }
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)) {
                    ChartLabel(text = "Last 7 days")
                    ProgressLabel(goal = goal,
                        progress = progress,
                        progressPercent = progressPercent)
                }
                ProgressBarChart(
                    color = cardColor ?: MaterialTheme.colors.primary,
                    firstDate = firstDate,
                    values = values)
            }
            GoalType.LongTerm -> {
                val model: AccumulatingRangeGoalProgressViewModel =
                    viewModel(key = "accumulatingDailyViewModel${goal.id}",
                        factory = AccumulatingRangeGoalProgressViewModel.Factory(goal.id.toString()))
                val perDayChartMap by model.perDayChartMap.observeAsState()
                val chartRange by model.chartRange.observeAsState()
                chartRange?.let { range ->

                    val days = ChronoUnit.DAYS.between(range.lower, range.upper).toInt() + 1
                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)) {
                        ChartLabel(text = "Last $days days")
                        ProgressLabel(goal = goal,
                            progress = progress,
                            progressPercent = progressPercent)
                    }
                    ProgressLineChart(
                        color = cardColor ?: MaterialTheme.colors.primary,
                        firstDate = range.lower,
                        dayCount = days, // 30,
                        values = perDayChartMap.orEmpty()
                    )
                }
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
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
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