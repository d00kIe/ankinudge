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
fun ActivityRow(activity: Activity, onClick: (id: String) -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical = 8.dp).clickable(onClick = { onClick(activity.id.toString()) }),
        elevation = 2.dp)
    {
        ListItem(
            icon = { ActivityRowIcon(activity.type?.category?.icon, activity.confidence, activity.motivation, activity.type?.category?.color) },
            text = { Text(activity.title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
            secondaryText = { OverlineText(activity.startTime, activity.endTime, activity.language, activity.type?.name) })
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