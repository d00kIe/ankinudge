package com.teraculus.lingojournalandroid.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.activityData
import com.teraculus.lingojournalandroid.model.activityTypeData
import com.teraculus.lingojournalandroid.ui.components.ConfidenceMotivationIndicator
import com.teraculus.lingojournalandroid.utils.toDateNoYearString
import com.teraculus.lingojournalandroid.utils.toDateString
import com.teraculus.lingojournalandroid.utils.toTimeString

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    model: ActivityListViewModel = viewModel("activityListViewModel",
        ActivityListViewModelFactory()),
    onItemClick: (id: String) -> Unit,
) {
    val activities: List<Activity>? by model.activities.observeAsState()
    ActivityList(activities = activities, onItemClick)
}

@ExperimentalMaterialApi
@Preview
@Composable
fun PreviewActivityList() {
    val fakeActivities = activityData(activityTypeData())
    ActivityList(activities = fakeActivities, onItemClick = {})
}

@ExperimentalMaterialApi
@Composable
fun ActivityList(activities: List<Activity>?, onItemClick: (id: String) -> Unit) {
    val groups = activities?.groupBy { it.date }
    LazyColumn(
    ) {
        item {
            Text("Journal", modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp), style = MaterialTheme.typography.h4)
        }
        if (activities != null) {
            groups?.forEach { (date, items) ->
                item {
                    Column {
                        Text(text = toDateNoYearString(date), style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
                        Divider(Modifier.padding(bottom = 8.dp))
                    }
                }
                items(items) { message ->
                    ActivityRow(message, onClick = onItemClick)
                }
            }
        } else {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Add some activity, dude!", style = MaterialTheme.typography.h5)
                }
            }
        }
        item {
            Surface(Modifier.padding(bottom = 200.dp)) {
            }
        }

    }
}

@ExperimentalMaterialApi
@Composable
fun ActivityRow(activity: Activity, onClick: (id: String) -> Unit) {
    val icon : Int = activity.type?.category?.icon!!
    Card(elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(activity.id.toString()) })) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            ListItem(
                icon = { ActivityRowIcon(icon, activity.confidence, activity.motivation) },
                text = { Text(activity.title)},
                secondaryText = { if(activity.text.isNotEmpty()) Text(activity.text) else null },
                overlineText = { OverlineText(activity) })
        }
    }
}
@Composable
fun OverlineText(activity: Activity) {
    val text = "${toTimeString(activity.startTime)} · ${getLanguageDisplayName(activity.language)} · ${activity.type?.name}"
    Text(text)
}

@Composable
fun ActivityRowIcon(icon: Int, confidence: Float, motivation: Float) {
    Surface(elevation = 1.dp,modifier = Modifier.size(48.dp), shape = CircleShape) {
        ConfidenceMotivationIndicator(confidence = confidence, motivation = motivation)

        Icon(painter = painterResource(id = icon), modifier = Modifier
            .padding(12.dp)
            .alpha(ContentAlpha.medium),  contentDescription = null)
    }
}