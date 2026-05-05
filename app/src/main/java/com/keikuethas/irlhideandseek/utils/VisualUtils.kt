package com.keikuethas.irlhideandseek.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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