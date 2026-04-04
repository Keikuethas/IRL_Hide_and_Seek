package com.keikuethas.irlhideandseek

import java.util.UUID

data class GameRule (
    val id: String = UUID.randomUUID().toString(),
    val localID: Int,


)