package com.teraculus.lingojournalandroid.ui.goals

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.ActivityGoal
import com.teraculus.lingojournalandroid.model.EffortUnit
import com.teraculus.lingojournalandroid.utils.getDurationString
import com.teraculus.lingojournalandroid.utils.toActivityTypeCategoryName
import com.teraculus.lingojournalandroid.utils.toShortActivityTypeTitle
import com.teraculus.lingojournalandroid.viewmodel.GoalItemViewModel
import com.teraculus.lingojournalandroid.viewmodel.GoalItemViewModelFactory
import com.teraculus.lingojournalandroid.viewmodel.GoalsListViewModel
import com.teraculus.lingojournalandroid.viewmodel.GoalsListViewModelFactory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeGoalsCard(
    onOpenGoals: () -> Unit,
    onGoalClick: (goalId: String) -> Unit,
    goalModel: GoalsListViewModel = viewModel("goalsListViewModel",
        GoalsListViewModelFactory()),) {

    val todayGoals by goalModel.todayGoals.observeAsState()
    val hasGoals by goalModel.hasGoals.observeAsState()

    if(hasGoals == false) {
        Card(
            modifier = Modifier
            .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = { onOpenGoals() }) {
            Column() {
                ListItem(
                    //icon = { Icon(Icons.Rounded.AddTask, contentDescription = null) },
                    text = { Text("Set goals") },
                    secondaryText = {
                        Text("It is well known that setting realistic but challenging goals improves motivation and performance. Set daily and long-term goals.")
                    },
                    trailing = {
                        IconButton(onClick = { onOpenGoals() }) {
                            Icon(Icons.Rounded.KeyboardArrowRight,
                                contentDescription = null)
                        }
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
    else {
        Card(modifier = Modifier
            .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp)) {
            Column {
                ListItem(
                    modifier = Modifier.clickable { onOpenGoals() },
                    text = { Text("Your goals") },
                    secondaryText = {
                        if (todayGoals.isNullOrEmpty()) {
                            Text("No goals set for today")
                        } else {
                            Text("Tap on the goal to add activity")
                        }
                    },
                    trailing = {
                        IconButton(onClick = { onOpenGoals() }) {
                            Icon(Icons.Rounded.KeyboardArrowRight,
                                contentDescription = null)
                        }
                    }
                )

                if (!todayGoals.isNullOrEmpty()) {
                    todayGoals.orEmpty().forEach { goal ->
                        FeedGoalRow(goal, onClick = onGoalClick)
                    }
                }
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

    //val dueBy = if(goal.type == GoalType.Daily) goalWeekDaysString.orEmpty() else "Due by ${toDayStringOrToday(goal.endDate)}"
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
        val progress by model.progress.observeAsState()
        val progressPercent by model.progressPercent.observeAsState()
        Column() {
            ListItem(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .clickable { onClick(goal.id.toString()) },
                overlineText = { Text("${goal.type.title} goal · ${toActivityTypeCategoryName(goal.activityType)}")
                },
                text = {
                    Text(toShortActivityTypeTitle(goal.activityType),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                },
                secondaryText = {
                    Column() {
                        SecondaryText(goal, progress, progressPercent)
                        Surface(shape = RoundedCornerShape(4.dp)) {
                            LinearProgressIndicator(
                                progress = (progressPercent ?: 0f) / 100f,
                                color = cardColor ?: MaterialTheme.colors.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp))
                        }
                    }

                },
                trailing = {
                    Box(Modifier.fillMaxHeight(), Alignment.Center) {
                        IconButton(onClick = { onClick(goal.id.toString()) }) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}