package com.teraculus.lingojournalandroid.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.activityData
import com.teraculus.lingojournalandroid.model.activityTypeData

@Composable
fun HomeScreen(
    model: ActivityListViewModel = viewModel("activityListViewModel",
        ActivityListViewModelFactory()),
    onItemClick: (id: String) -> Unit,
) {
    val activities: List<Activity>? by model.activities.observeAsState()
    ActivityList(activities = activities, onItemClick)
}

@Preview
@Composable
fun PreviewActivityList() {
    val fakeActivities = activityData(activityTypeData())
    ActivityList(activities = fakeActivities, onItemClick = {})
}

@Composable
fun ActivityList(activities: List<Activity>?, onItemClick: (id: String) -> Unit) {

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (activities != null) {
            items(activities) { message ->
                ActivityRow(message, onClick = onItemClick)
            }
        } else {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Add some activity, dude!", style = MaterialTheme.typography.h5)
                }
            }
        }

    }
}

@Composable
fun ActivityRow(activity: Activity, onClick: (id: String) -> Unit) {
    Card(elevation = 0.dp,
        modifier = Modifier.clickable(onClick = { onClick(activity.id.toString()) })) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = activity.title, style = MaterialTheme.typography.h6)
            Text(text = activity.text, style = MaterialTheme.typography.body1)
        }
    }
}