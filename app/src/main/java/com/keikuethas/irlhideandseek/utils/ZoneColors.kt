package com.keikuethas.irlhideandseek.utils

import androidx.compose.ui.graphics.Color
import com.keikuethas.irlhideandseek.model.ZoneType

val ZoneType.strokeColor: Color get() = when(this) {
    ZoneType.SAFE -> Color(3, 169, 244, 255)
    ZoneType.DANGER -> Color(233, 30, 30, 255)
    ZoneType.WARNING -> Color(255, 152, 0, 255)
    ZoneType.AIRDROP -> Color(255, 235, 59, 255)
    ZoneType.SNARE -> Color(126, 124, 124, 255)
    ZoneType.TRAP -> Color(0, 0, 0, 255)
    ZoneType.SAFE_HOUSE -> Color(0, 0,0)
    ZoneType.SAFE_MANSION -> Color(0, 0,0)
}

val ZoneType.fillColor: Color get() = this.strokeColor.copy(alpha = 0.05F)