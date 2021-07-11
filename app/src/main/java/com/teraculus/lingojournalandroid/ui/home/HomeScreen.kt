package com.teraculus.lingojournalandroid.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.ui.ads.AdBanner
import com.teraculus.lingojournalandroid.ui.ads.rememberAdViewState
import com.teraculus.lingojournalandroid.ui.components.ActivityRow
import com.teraculus.lingojournalandroid.ui.goals.HomeGoalsCard
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.toDayStringOrToday
import com.teraculus.lingojournalandroid.viewmodel.ActivityListViewModel
import com.teraculus.lingojournalandroid.viewmodel.ActivityListViewModelFactory
import com.teraculus.lingojournalandroid.viewmodel.BillingViewModel

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    model: ActivityListViewModel = viewModel(key = "activityListViewModel",
        factory = ActivityListViewModelFactory()),
    onItemClick: (id: String) -> Unit,
    onOpenStats: () -> Unit,
    scrollState: LazyListState,
    onGoalClick: (goalId: String) -> Unit,
    onOpenGoals: () -> Unit
) {
    ActivityList(model, onItemClick, onOpenStats, scrollState, onGoalClick, onOpenGoals)
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
    billingModel: BillingViewModel = viewModel(key = "billingViewModel"),
) {
    val groups by model.grouped.observeAsState()
    val canPurchase by billingModel.canPurchase.observeAsState()

    val width = LocalConfiguration.current.screenWidthDp.toInt()
    val adViewState = rememberAdViewState(model.adRequest, width - 64)

    LazyColumn(state = scrollState) {
        item(key = "homeStatsCard") {
            HomeStatsCard(onOpenStats, model = model)
        }

        if(canPurchase == true) {
            item(key = "adCard") {
                AdBanner(
                    Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    adViewState
                )
            }
        }

        item(key = "homeGoalsCard") {
            HomeGoalsCard(onOpenGoals, onGoalClick)
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