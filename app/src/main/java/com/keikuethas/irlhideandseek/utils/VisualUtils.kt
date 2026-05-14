package com.keikuethas.irlhideandseek.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.keikuethas.irlhideandseek.Ability

fun Color.adjustLightness(delta: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[2] = (hsl[2] + delta).coerceIn(0f, 1f) // delta: +0.2 = светлее, -0.2 = темнее
    return Color(ColorUtils.HSLToColor(hsl))
}

fun makeGradientAbilityBrush(ability: Ability, progress: Float): Brush {
    val baseColor: Color = ability.color
    val lightnessDelta = 0.4f
    return Brush.linearGradient(
        colorStops = arrayOf(
            progress to baseColor,
            progress + (1f - progress) / 2 to baseColor.adjustLightness(-lightnessDelta),
            1f to baseColor.adjustLightness(-lightnessDelta * 2)
        )
    )
}

/**
 * Пунктирная граница с заданным цветом
 */
fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    dashLength: Dp,
    gapLength: Dp,
    cornerRadius: Dp = 0.dp
) = drawBehind {
    val strokeWidthPx = width.toPx()
    val dashPx = dashLength.toPx()
    val gapPx = gapLength.toPx()
    val radiusPx = cornerRadius.toPx()

    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(dashPx, gapPx),
        phase = 0f
    )

    drawRoundRect(
        color = color,
        topLeft = Offset.Zero,
        size = size,
        cornerRadius = CornerRadius(radiusPx, radiusPx),
        style = Stroke(
            width = strokeWidthPx,
            pathEffect = pathEffect
        )
    )
}

/**
 * Пунктирная граница с заданной кистью.
 * Градиент/текстура применяется к видимым участкам пунктира,
 * а промежутки остаются прозрачными.
 */
fun Modifier.dashedBorder(
    width: Dp,
    brush: Brush,
    dashLength: Dp,
    gapLength: Dp,
    cornerRadius: Dp = 0.dp
) = drawBehind {
    val strokeWidthPx = width.toPx()
    val dashPx = dashLength.toPx()
    val gapPx = gapLength.toPx()
    val radiusPx = cornerRadius.toPx()

    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(dashPx, gapPx),
        phase = 0f
    )

    drawRoundRect(
        brush = brush,
        topLeft = Offset.Zero,
        size = size,
        cornerRadius = CornerRadius(radiusPx, radiusPx),
        style = Stroke(
            width = strokeWidthPx,
            pathEffect = pathEffect
        )
    )
}