package com.keikuethas.irlhideandseek.ui.theme

import androidx.compose.ui.graphics.Color
import com.keikuethas.irlhideandseek.RoleType

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val RoleType.color: Color
    get() = when (this) {
        RoleType.Seeker -> Color(233, 30, 99, 255)
        RoleType.Hider -> Color(33, 150, 243, 255)
    }