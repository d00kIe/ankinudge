package com.teraculus.lingojournalandroid.ui.home

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.utils.toDayString
import com.teraculus.lingojournalandroid.utils.toTimeString
import java.time.LocalTime

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    model: ActivityListViewModel = viewModel("activityListViewModel",
        ActivityListViewModelFactory()),
    onItemClick: (id: String) -> Unit,
) {
    ActivityList(model = model, onItemClick)
}

@ExperimentalMaterialApi
@Composable
fun ActivityList(
    model: ActivityListViewModel,
    onItemClick: (id: String) -> Unit
) {
    val groups by model.grouped.observeAsState()

    LazyColumn(
    ) {
        item {
            Text("Journal", modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp), style = MaterialTheme.typography.h4)
        }
        if (groups != null && groups.orEmpty().isNotEmpty()) {
            groups.orEmpty().forEach { (date, items) ->
                item {
                    Column {
                        Text(text = toDayString(date), style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
                        Divider(Modifier.padding(bottom = 8.dp))
                    }
                }
                items(items.filter { it.isValid }) { activity ->
                    ActivityRow(activity, onClick = onItemClick)
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
    val model =  viewModel<ActivityItemViewModel>("activityRow${activity.id}", ActivityItemViewModelFactory(activity))
    val title by model.title.observeAsState()
    val text by model.text.observeAsState()
    val type by model.type.observeAsState()
    val category by model.category.observeAsState()
    val startTime by model.startTime.observeAsState()
    val language by model.language.observeAsState()
    val confidence by model.confidence.observeAsState()
    val motivation by model.motivation.observeAsState()
    Card(elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(activity.id.toString()) })) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)) {
            ListItem(
                icon = { ActivityRowIcon(category?.icon, confidence, motivation, category?.color) },
                text = { Text(title.orEmpty())},
                secondaryText = { if(text.orEmpty().isNotEmpty()) Text(text.orEmpty()) },
                overlineText = { OverlineText(startTime, language, type?.name) })
        }
    }
}
@Composable
fun OverlineText(startTime: LocalTime?, language: String?, typeName: String?) {
    val text = "${toTimeString(startTime)} · ${getLanguageDisplayName(language.orEmpty())} · ${ typeName.orEmpty() }"
    Text(text)
}

@Composable
fun ActivityRowIcon(icon: Int?, confidence: Float?, motivation: Float?, color: Int?) {
    if(icon != null && confidence != null && motivation != null && color != null)
        Surface(elevation = 1.dp,modifier = Modifier.size(48.dp), shape = CircleShape, color = Color(color)) {
            //ConfidenceMotivationIndicator(confidence = confidence, motivation = motivation)

            Icon(painter = painterResource(id = icon), modifier = Modifier
                .padding(12.dp), tint = MaterialTheme.colors.onPrimary, contentDescription = null)
        }
}