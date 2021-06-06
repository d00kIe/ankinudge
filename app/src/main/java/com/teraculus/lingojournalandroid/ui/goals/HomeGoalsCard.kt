package com.teraculus.lingojournalandroid.ui.goals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddTask
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.ui.stats.StatsCard
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
        StatsCard(modifier = Modifier
            .padding(horizontal = 16.dp),
            onClick = { onOpenGoals() }) {
            Column() {
                ListItem(
                    icon = { Icon(Icons.Rounded.AddTask, contentDescription = null) },
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
        StatsCard(modifier = Modifier
            .padding(horizontal = 16.dp)) {
            Column {
                ListItem(
                    modifier = Modifier.clickable { onOpenGoals() },
                    text = { Text("Your goals") },
                    secondaryText = {
                        if (todayGoals.isNullOrEmpty()) {
                            Text("No goals set for today")
                        } else {
                            Text("Use plus icon to add goal activity.")
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