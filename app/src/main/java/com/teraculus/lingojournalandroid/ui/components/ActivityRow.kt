package com.teraculus.lingojournalandroid.ui.components

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
import com.teraculus.lingojournalandroid.utils.toTimeString
import java.time.LocalTime


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
    ListItem(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable(onClick = { onClick(activity.id.toString()) }),
        icon = { ActivityRowIcon(category?.icon, confidence, motivation, category?.color) },
        text = { Text(title.orEmpty()) },
        secondaryText = { if(text.orEmpty().isNotEmpty()) Text(text.orEmpty(), maxLines = 3, overflow = TextOverflow.Ellipsis) },
        overlineText = { OverlineText(startTime, language, type?.name) })

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