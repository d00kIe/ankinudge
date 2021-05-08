package com.teraculus.lingojournalandroid.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import com.teraculus.lingojournalandroid.ui.components.ActivityRow
import com.teraculus.lingojournalandroid.ui.goals.FeedGoalRow
import com.teraculus.lingojournalandroid.ui.stats.StatsCard
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.toDayStringOrToday
import java.time.LocalDate

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    model: ActivityListViewModel = viewModel("activityListViewModel",
        ActivityListViewModelFactory()),
    onItemClick: (id: String) -> Unit,
    onOpenStats: () -> Unit,
    scrollState: LazyListState,
    onGoalClick: (goalId: String) -> Unit,
    onOpenGoals: () -> Unit
) {
    ActivityList(model = model, onItemClick, onOpenStats, scrollState, onGoalClick = onGoalClick, onOpenGoals = onOpenGoals)
}

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@Composable
fun ActivityList(
    model: ActivityListViewModel,
    onItemClick: (id: String) -> Unit,
    onOpenStats: () -> Unit,
    scrollState: LazyListState,
    onGoalClick: (goalId: String) -> Unit,
    onOpenGoals: () -> Unit,
) {
    val groups by model.grouped.observeAsState()
    val today = LocalDate.now()
    val todayGoals by model.todayGoals.observeAsState()
    val hasGoals by model.hasGoals.observeAsState()

    LazyColumn(state = scrollState) {
        item {
            HomeStatsCard(onOpenStats, model = model)
        }

        if(hasGoals == false && groups.orEmpty().size < 3) {
            item {
                StatsCard(modifier = Modifier.padding(horizontal = 16.dp).clickable { onOpenGoals() }) {
                    ListItem(
                        icon = { Icon(Icons.Rounded.AddTask, contentDescription = null) },
                        text = { Text("Set daily goals") },
                        trailing = {
                            Icon(Icons.Rounded.KeyboardArrowRight,
                                contentDescription = null)
                        }
                    )
                }
            }
        }

        if(!groups.orEmpty().containsKey(today) && !todayGoals.isNullOrEmpty()) {
            item {
                ApplyTextStyle(textStyle = MaterialTheme.typography.body2, contentAlpha = ContentAlpha.medium) {
                    Text(text = "Today", modifier = Modifier.padding(top=16.dp, start = 16.dp))
                }
            }
            items(todayGoals.orEmpty()) { goal ->
                FeedGoalRow(goal, onClick = onGoalClick)
            }
        }
        if (groups != null && groups.orEmpty().isNotEmpty()) {
            groups.orEmpty().forEach { (date, items) ->
                item {
                    ApplyTextStyle(textStyle = MaterialTheme.typography.body2, contentAlpha = ContentAlpha.medium) {
                        Text(text = toDayStringOrToday(date), modifier = Modifier.padding(top=16.dp, start = 16.dp))
                    }
                }
                if (date == today) {
                    items(todayGoals.orEmpty()) { goal ->
                        FeedGoalRow(goal, onClick = onGoalClick)
                    }
                }
                items(items) { activity ->
                    ActivityRow(activity, onClick = onItemClick)
                }
            }
        }
        item {
            Spacer(Modifier.size(200.dp))
        }
    }
}