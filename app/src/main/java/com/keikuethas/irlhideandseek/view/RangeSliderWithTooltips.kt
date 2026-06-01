package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private sealed class ActiveThumb {
    object Start : ActiveThumb()
    object End : ActiveThumb()
}

@Composable
fun RangeSliderWithTooltips(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    startLabel: String = "От ",
    endLabel: String = "До ",
    valueFormatter: (Float) -> String = { it.roundToInt().toString() },
    label: @Composable () -> Unit = {}
) {
    var activeThumb by remember { mutableStateOf<ActiveThumb?>(null) }
    var sliderBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    var bubbleWidthPx by remember { mutableStateOf(0f) }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val currentValue by rememberUpdatedState(value)

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val fallbackWidthPx = with(density) { 100.dp.toPx() }
    val halfBubblePx = maxOf(bubbleWidthPx, fallbackWidthPx) / 2f

    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier.fillMaxWidth().height(80.dp)
    ) {
        // Текст под пузырьком
        Box(modifier = Modifier.align(Alignment.Center).padding(bottom = 48.dp)) {
            label()
        }

        // --- Start Bubble ---
        if (activeThumb is ActiveThumb.Start && sliderBounds != null) {
            val percentage = (currentValue.start - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            val thumbRelX = sliderBounds!!.width * percentage
            val thumbDp = with(density) { thumbRelX.toDp() }
            val halfBubbleDp = with(density) { halfBubblePx.toDp() }
            val halfSliderDp = with(density) { (sliderBounds!!.width / 2f).toDp() }

            TooltipBubble(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = thumbDp - halfSliderDp + halfBubbleDp, y = (-40).dp),
                label = startLabel,
                value = valueFormatter(currentValue.start),
                onSizeChanged = { bubbleWidthPx = it },
                backgroundColor = colors.surfaceContainerHigh,
                labelColor = colors.onSurfaceVariant,
                valueColor = colors.onSurface
            )
        }

        // --- End Bubble ---
        if (activeThumb is ActiveThumb.End && sliderBounds != null) {
            val percentage = (currentValue.endInclusive - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            val thumbRelX = sliderBounds!!.width * percentage
            val thumbDp = with(density) { thumbRelX.toDp() }
            val halfBubbleDp = with(density) { halfBubblePx.toDp() }
            val halfSliderDp = with(density) { (sliderBounds!!.width / 2f).toDp() }

            TooltipBubble(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = thumbDp - halfSliderDp - halfBubbleDp, y = (-40).dp),
                label = endLabel,
                value = valueFormatter(currentValue.endInclusive),
                onSizeChanged = { bubbleWidthPx = it },
                backgroundColor = colors.surfaceContainerHigh,
                labelColor = colors.onSurfaceVariant,
                valueColor = colors.onSurface
            )
        }

        // ✅ Добавлен onSizeChanged для инициализации bounds и расчёта позиции пузырька
        RangeSlider(
            value = currentValue,
            onValueChange = { newValue ->
                if (newValue.start != currentValue.start) activeThumb = ActiveThumb.Start
                else if (newValue.endInclusive != currentValue.endInclusive) activeThumb = ActiveThumb.End
                onValueChange(newValue)
            },
            onValueChangeFinished = { activeThumb = null },
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onSizeChanged { coords ->
                    sliderBounds = androidx.compose.ui.geometry.Rect(0f, 0f, coords.width.toFloat(), coords.height.toFloat())
                },
            colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                activeTrackColor = colors.primary,
                inactiveTrackColor = colors.surfaceVariant,
            )
        )
    }
}

@Composable
private fun TooltipBubble(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onSizeChanged: (Float) -> Unit,
    backgroundColor: Color,
    labelColor: Color,
    valueColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .onSizeChanged { size -> onSizeChanged(size.width.toFloat()) }
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = label, color = labelColor, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = valueColor, fontSize = 16.sp, lineHeight = 20.sp)
    }
}