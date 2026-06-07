package com.keikuethas.irlhideandseek.ui.theme

import androidx.compose.ui.graphics.Color
import com.keikuethas.irlhideandseek.model.RoleType

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


// REFACTOR: theme

val RoleType.color: Color
    get() = when (this) {
        RoleType.SEEKER -> Color(233, 30, 99, 255)
        RoleType.HIDER -> Color(33, 150, 243, 255)
    }

val RoleType.surfaceColor: Color
    get() = when (this) {
        RoleType.SEEKER -> Color(236, 176, 196, 255)
        RoleType.HIDER -> Color(183, 215, 239, 255)
    }

val BarelyGrey = Color(220, 220, 220, 255)