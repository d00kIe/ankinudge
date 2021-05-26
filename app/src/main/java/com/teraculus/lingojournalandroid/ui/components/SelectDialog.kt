package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.teraculus.lingojournalandroid.data.Language
import com.teraculus.lingojournalandroid.data.getAllLanguages
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.model.MeasurementUnit
import com.teraculus.lingojournalandroid.model.UserPreferences
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SelectDialog(
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            shape = RoundedCornerShape(8.dp)) {
            Column {
                Text(text = title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(16.dp))
                content()
            }
        }
    }
}

@Composable
fun InputDialog(
    onConfirm: (value: String) -> Unit,
    onDismissRequest: () -> Unit,
    title: String,
) {
    var value by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title,
                    style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(value = value,
                    onValueChange = { value = it },
                    label = { Text("New activity type") },
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.size(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text(text = "Cancel") }
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(enabled = value.isNotBlank(),
                        onClick = { onConfirm(value) }) { Text(text = "Add") }
                }
            }
        }
    }
}

@Composable
fun NewActivityTypeDialog(
    onConfirm: (value: String, unit: MeasurementUnit) -> Unit,
    onDismissRequest: () -> Unit,
    title: String,
) {
    var value by remember { mutableStateOf("") }
    var unitValue by remember { mutableStateOf(MeasurementUnit.Time) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title,
                    style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(value = value,
                    onValueChange = { value = it },
                    label = { Text("New activity type") },
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.size(16.dp))
                ApplyTextStyle(MaterialTheme.typography.caption, ContentAlpha.medium) {
                    Text("Measurement unit")
                }
                ScrollableTabRow(
                    selectedTabIndex = MeasurementUnit.values().indexOfFirst { u -> u == unitValue }
                        .coerceAtLeast(0),
                    backgroundColor = MaterialTheme.colors.surface,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = {}) {
                    MeasurementUnit.values().forEach { unit ->
                        ToggleButton(onClick = { unitValue = unit },
                            selected = unit == unitValue,
                            modifier = Modifier.padding(8.dp),
                            highlighted = true) {
                            Text(unit.title)
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text(text = "Cancel") }
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(enabled = value.isNotBlank(),
                        onClick = { onConfirm(value, unitValue) }) { Text(text = "Add") }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LanguageSelectDialog(
    onItemClick: (item: Language) -> Unit,
    onDismissRequest: () -> Unit,
    preferences: UserPreferences?,
) {
    val languages by remember {
        mutableStateOf(getAllLanguages().filter { preferences?.languages?.contains(it.code) == false })
    }
    val usedLanguages by remember {
        mutableStateOf(getAllLanguages().filter { preferences?.languages?.contains(it.code) == true })
    }
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = "Language",
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (usedLanguages.isNotEmpty()) {
                items(usedLanguages) { item ->
                    LanguageItem(item, onClick = onItemClick, isRecent = true)
                }
                item {
                    Divider()
                }
            }
            items(languages) { item ->
                LanguageItem(item, onClick = onItemClick)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LanguageItem(lang: Language, onClick: (item: Language) -> Unit, isRecent: Boolean = false) {
    ListItem(
        text = { Text(lang.name) },
        modifier = Modifier.clickable { onClick(lang) },
        trailing = {
            if (isRecent)
                Icon(Icons.Rounded.History, contentDescription = null)
        })
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityTypeSelectDialog(
    onItemClick: (item: ActivityType) -> Unit,
    onAddTypeClick: (item: ActivityType) -> Unit,
    onDismissRequest: () -> Unit,
    groups: State<Map<ActivityCategory?, List<ActivityType>>?>,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var dialogCategory: ActivityCategory? by remember { mutableStateOf(null) }

    if (showAddDialog && dialogCategory != null)
        NewActivityTypeDialog(
            onConfirm = { name, unit ->
                onAddTypeClick(ActivityType(dialogCategory, name, unit = unit))
                showAddDialog = false
                dialogCategory = null
            },
            onDismissRequest = { showAddDialog = false },
            title = "New ${dialogCategory?.title} activity")

    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = "Activity type",
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            groups.value.orEmpty().forEach { (category, categoryTypes) ->
                stickyHeader {
                    ActivityTypeHeader(category = category) {
                        dialogCategory = it
                        showAddDialog = true
                    }
                }
                items(categoryTypes) { item ->
                    ActivityTypeItem(item, onClick = onItemClick)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityTypeHeader(
    category: ActivityCategory?,
    onAddTypeClick: (item: ActivityCategory) -> Unit,
) {

    if (category != null)
        Surface {
            Column {
                ListItem(
                    text = {
                        Text(category.title,
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold)
                    },
                    icon = {
                        ActivityTypeIcon(category = category)
                    },
                    trailing = {
                        OutlinedButton(
                            onClick = { onAddTypeClick(category) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSurface)) {
                            Text("Add")
                        }
                    }
                )
                Divider()
            }
        }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityTypeItem(type: ActivityType, onClick: (item: ActivityType) -> Unit) {
    ListItem(
        text = { Text(type.name) },
        secondaryText = { Text("Unit: ${type.unit!!.title}") },
        modifier = Modifier.clickable { onClick(type) })
}

@ExperimentalMaterialApi
@Composable
fun RadioSelectDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (text: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = title,
    ) {
        Column(Modifier.selectableGroup()) {
            options.forEach { text ->
                RadioWithText(text, text == selected, onSelect)
            }
        }
    }
}

@Composable
private fun RadioWithText(
    text: String,
    selected: Boolean,
    onSelect: (text: String) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = { onSelect(text) },
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null recommended for accessibility with screenreaders
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1.merge(),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun DurationPicker(
    onDismissRequest: () -> Unit,
    hours: Int?,
    minutes: Int?,
    onChange: (Int?, Int?) -> Unit,
) {
    var h by remember(hours) { mutableStateOf(hours) }
    var m by remember(minutes) { mutableStateOf(minutes) }
    SelectDialog(onDismissRequest = { onDismissRequest() }, title = "Duration") {
        Column() {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.Center) {
                NumberPicker(value = h ?: 0,
                    range = 0..1000,
                    textStyle = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    label = "h") {
                    h = it
                }
                NumberPicker(value = m ?: 0,
                    range = 0..59,
                    textStyle = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    label = "m") {
                    m = it
                }

            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismissRequest) { Text(text = "Cancel") }
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = { onChange(h, m) }) { Text(text = "Done") }
            }
        }
    }
}

@Composable
fun NumberPicker(
    value: Int,
    modifier: Modifier = Modifier,
    range: IntRange? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    size: Dp = 32.dp,
    label: String = "",
    onValueChange: (Int) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val numbersColumnHeightPx =
        with(LocalDensity.current) { size.toPx() }

    fun animatedStateValue(offset: Float): Int =
        value - (offset / numbersColumnHeightPx).toInt()

    val animatedOffset = remember { Animatable(0f) }.apply {
        if (range != null) {
            val offsetRange = remember(value, range) {
                val v = value
                val first = -(range.last - v) * numbersColumnHeightPx
                val last = -(range.first - v) * numbersColumnHeightPx
                first..last
            }
            updateBounds(offsetRange.start, offsetRange.endInclusive)
        }
    }
    val coercedAnimatedOffset = animatedOffset.value % numbersColumnHeightPx
    val animatedStateValue = animatedStateValue(animatedOffset.value)
    val surfaceColor = LocalElevationOverlay.current?.apply(color = MaterialTheme.colors.surface,
        elevation = 1.dp * LocalAbsoluteElevation.current.value) ?: MaterialTheme.colors.surface
    Column(
        modifier = modifier
            .clipToBounds()
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay<Float>(frictionMultiplier = 1f),
                            adjustTarget = { target ->
                                val coercedTarget = target % numbersColumnHeightPx
                                val coercedAnchors = listOf(-numbersColumnHeightPx,
                                    0f,
                                    numbersColumnHeightPx)
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base =
                                    numbersColumnHeightPx * (target / numbersColumnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        onValueChange(animatedStateValue(endValue))
                        animatedOffset.snapTo(0f)
                    }
                }
            )
    ) {
        Box(modifier =
        Modifier
            .size(width = size * 3, height = size * 1.5f)
            .zIndex(1f)
            .background(Brush.verticalGradient(
                listOf(surfaceColor,
                    surfaceColor.copy(alpha = 0f)),
                startY = 0f,
                endY = numbersColumnHeightPx * 1.5f
            ))) {}

        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .zIndex(0f)) {

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .size(width = size * 3, height = size)
                    .zIndex(1f)) {
                Divider()
                Text(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp), text = label, textAlign = TextAlign.End)
                Divider()
            }
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size * 2)
                    .zIndex(0f)
                    .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
            ) {
                val baseLabelModifier = Modifier.align(Alignment.Center)
                ProvideTextStyle(textStyle) {

                    Text(text = if (range?.contains(animatedStateValue - 3) == true) (animatedStateValue - 3).toString() else "",
                        modifier = baseLabelModifier.offset(y = -size * 3))

                    Text(text = if (range?.contains(animatedStateValue - 2) == true) (animatedStateValue - 2).toString() else "",
                        modifier = baseLabelModifier.offset(y = -size * 2))

                    Text(text = if (range?.contains(animatedStateValue - 1) == true) (animatedStateValue - 1).toString() else "",
                        modifier = baseLabelModifier.offset(y = -size))

                    Text(text = animatedStateValue.toString(),
                        modifier = baseLabelModifier)

                    Text(text = if (range?.contains(animatedStateValue + 1) == true) (animatedStateValue + 1).toString() else "",
                        modifier = baseLabelModifier.offset(y = size))

                    Text(text = if (range?.contains(animatedStateValue + 2) == true) (animatedStateValue + 2).toString() else "",
                        modifier = baseLabelModifier.offset(y = size * 2))

                    Text(text = if (range?.contains(animatedStateValue + 3) == true) (animatedStateValue + 3).toString() else "",
                        modifier = baseLabelModifier.offset(y = size * 3))
                }
            }
        }

        Box(modifier =
        Modifier
            .size(width = size * 3, height = size * 1.5f)
            .background(Brush.verticalGradient(
                listOf(surfaceColor,
                    surfaceColor.copy(alpha = 0f)),
                startY = numbersColumnHeightPx * 1.5f,
                endY = 0f
            ))) {}
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)

    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}