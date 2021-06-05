package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.utils.getDurationString
import com.teraculus.lingojournalandroid.utils.getMeasurementUnitValueString
import com.teraculus.lingojournalandroid.utils.toActivityTypeTitle
import com.teraculus.lingojournalandroid.viewmodel.ActivityItemViewModel
import com.teraculus.lingojournalandroid.viewmodel.ActivityItemViewModelFactory


@ExperimentalMaterialApi
@Composable
fun ActivityRow(
    rawactivity: Activity,
    onClick: (id: String) -> Unit,
    model: ActivityItemViewModel = viewModel("activityRow${rawactivity.id}",
        ActivityItemViewModelFactory(rawactivity, LocalLifecycleOwner.current)),
) {
    val snapshot by model.snapshot.observeAsState()
    snapshot?.let { activity ->
        val title =
            if (activity.title.isEmpty()) toActivityTypeTitle(activity.type) else activity.title
        Card(
            onClick = { onClick(activity.id.toString()) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = 2.dp)
        {
            ListItem(
                icon = {
                    ActivityRowIcon(activity.type?.category?.icon,
                        activity.type?.category?.color)
                },
                text = { Text(title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                secondaryText = {
                    SecondaryText(
                        activity.duration,
                        activity.language,
                        activity.type,
                        activity.unitCount)
                })
        }
    }
}

@Composable
private fun SecondaryText(
    duration: Int?,
    language: String?,
    type: ActivityType?,
    unitCount: Float?,
) {
    val values = listOf(
        getDurationString(duration ?: 0),
        type?.unit?.let { unit -> getMeasurementUnitValueString(unit, unitCount ?: 0f) },
        getLanguageDisplayName(language.orEmpty()),
        type?.name)

    Text(modifier = Modifier.padding(bottom = 8.dp),
        text = values.filterNotNull().joinToString(separator = " · "),
        style = MaterialTheme.typography.body2)
}

@Composable
fun ActivityRowIcon(icon: Int?, color: Int?) {
    if (icon != null && color != null)
        Surface(elevation = 0.dp,
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = Color(color)) {
            Icon(painter = painterResource(id = icon), modifier = Modifier
                .padding(10.dp), tint = MaterialTheme.colors.onPrimary, contentDescription = null)
        }
}