package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


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