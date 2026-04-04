package com.keikuethas.irlhideandseek

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.keikuethas.irlhideandseek.view.game.adjustLightness
import java.util.UUID

data class Ability(
    val id: String = UUID.randomUUID().toString(),
    val abilityType:AbilityType = AbilityType.Unspecified,
    val json: String = "{}",

    // --- presets ---
    val icon: ImageVector = when(abilityType) {
        else -> Icons.Default.Warning
    },

    val color: Color = when(abilityType) {
        else -> Color.Cyan.adjustLightness(-0.2f)
    },

    val description: String = when(abilityType) {
        else -> "Неизвестно, что делает эта способность." +
                " Если бы было известно, в этом тексте было бы отражено описание функционала. "
    },
)

enum class AbilityType {
    Unspecified;

    override fun toString(): String {
        return when(this) {
            Unspecified -> "Неизвестно"
        }
    }
}
