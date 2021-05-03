package com.teraculus.lingojournalandroid.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.ui.components.ActivityRow
import com.teraculus.lingojournalandroid.ui.goals.FeedGoalRow
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.toDayString
import com.teraculus.lingojournalandroid.utils.toDayStringOrToday
import java.time.LocalDate

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    model: ActivityListViewModel = viewModel("activityListViewModel",
        ActivityListViewModelFactory()),
    onItemClick: (id: String) -> Unit,
    onOpenStats: () -> Unit,
    scrollState: LazyListState
) {
    ActivityList(model = model, onItemClick, onOpenStats, scrollState)
}

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@Composable
fun ActivityList(
    model: ActivityListViewModel,
    onItemClick: (id: String) -> Unit,
    onOpenStats: () -> Unit,
    scrollState: LazyListState,
) {
    val groups by model.grouped.observeAsState()
    val today = LocalDate.now()
    val todayGoals by model.todayGoals.observeAsState()

    LazyColumn(state = scrollState) {
        item {
            HomeStatsCard(onOpenStats, model = model)
        }

        if(!todayGoals.isNullOrEmpty()) {
            item {
                ApplyTextStyle(textStyle = MaterialTheme.typography.body2, contentAlpha = ContentAlpha.medium) {
                    Text(text = "Today's Goals", modifier = Modifier.padding(top=16.dp, start = 16.dp))
                }
            }
            items(todayGoals.orEmpty()) { goal ->
                FeedGoalRow(goal)
            }
        }
        if (groups != null && groups.orEmpty().isNotEmpty()) {
            groups.orEmpty().forEach { (date, items) ->
                item {
                    ApplyTextStyle(textStyle = MaterialTheme.typography.body2, contentAlpha = ContentAlpha.medium) {
                        Text(text = toDayStringOrToday(date), modifier = Modifier.padding(top=16.dp, start = 16.dp))
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