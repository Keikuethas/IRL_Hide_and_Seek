package com.keikuethas.irlhideandseek.utils

import androidx.compose.ui.graphics.Color
import com.keikuethas.irlhideandseek.model.ZoneType

val ZoneType.strokeColor: Color get() = when(this) {
    ZoneType.SAFE -> TODO()
    ZoneType.DANGER -> TODO()
    ZoneType.WARNING -> TODO()
    ZoneType.AIRDROP -> TODO()
    ZoneType.SNARE -> TODO()
    ZoneType.TRAP -> TODO()
    ZoneType.SAFE_HOUSE -> TODO()
    ZoneType.SAFE_MANSION -> TODO()
}

val ZoneType.fillColor: Color get() = when(this) {
    ZoneType.SAFE -> TODO()
    ZoneType.DANGER -> TODO()
    ZoneType.WARNING -> TODO()
    ZoneType.AIRDROP -> TODO()
    ZoneType.SNARE -> TODO()
    ZoneType.TRAP -> TODO()
    ZoneType.SAFE_HOUSE -> TODO()
    ZoneType.SAFE_MANSION -> TODO()
}