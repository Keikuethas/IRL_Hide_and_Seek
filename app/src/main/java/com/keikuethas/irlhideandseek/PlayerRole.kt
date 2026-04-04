package com.keikuethas.irlhideandseek

import java.util.UUID

enum class PlayerRole {
    Hider, Seeker
}

// REFACTOR
data class PlayerRoleNEW(
    val name: String,
    //TODO: indication
    val id: String = UUID.randomUUID().toString()
) {

}