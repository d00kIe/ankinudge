package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.ui.home.ActivityItemViewModel
import com.teraculus.lingojournalandroid.ui.home.ActivityItemViewModelFactory
import com.teraculus.lingojournalandroid.utils.getDurationString
import com.teraculus.lingojournalandroid.utils.getMinutes
import com.teraculus.lingojournalandroid.utils.toTimeString
import java.time.LocalTime


@ExperimentalMaterialApi
@Composable
fun ActivityRow(activity: Activity, onClick: (id: String) -> Unit, model: ActivityItemViewModel = viewModel("activityRow${activity.id}", ActivityItemViewModelFactory(activity))) {
    val title by model.title.observeAsState()
    val text by model.text.observeAsState()
    val type by model.type.observeAsState()
    val category by model.category.observeAsState()
    val startTime by model.startTime.observeAsState()
    val endTime by model.endTime.observeAsState()
    val language by model.language.observeAsState()
    val confidence by model.confidence.observeAsState()
    val motivation by model.motivation.observeAsState()
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical = 8.dp).clickable(onClick = { onClick(activity.id.toString()) }),
        elevation = 2.dp)
    {
        ListItem(
            icon = { ActivityRowIcon(category?.icon, confidence, motivation, category?.color) },
            text = { Text(title.orEmpty(), maxLines = 2, overflow = TextOverflow.Ellipsis) },
            secondaryText = { OverlineText(startTime, endTime, language, type?.name) })
    }

}

@Composable
fun SecondaryText(text: String?) {
    if(text.orEmpty().isNotEmpty())
        Text(text.orEmpty(), maxLines = 3, overflow = TextOverflow.Ellipsis)
}

@Composable
fun OverlineText(startTime: LocalTime?, endTime: LocalTime?, language: String?, typeName: String?) {
    val text = "${getDurationString(getMinutes(startTime, endTime))} · ${getLanguageDisplayName(language.orEmpty())} · ${ typeName.orEmpty() }"
    Text(modifier = Modifier.padding(bottom = 8.dp), text = text, style = MaterialTheme.typography.caption)
}

@Composable
fun ActivityRowIcon(icon: Int?, confidence: Float?, motivation: Float?, color: Int?) {
    if(icon != null && confidence != null && motivation != null && color != null)
        Surface(elevation = 0.dp,modifier = Modifier.size(42.dp), shape = CircleShape, color = Color(color)) {
            //ConfidenceMotivationIndicator(confidence = confidence, motivation = motivation)

            Icon(painter = painterResource(id = icon), modifier = Modifier
                .padding(8.dp), tint = MaterialTheme.colors.onPrimary, contentDescription = null)
        }
}