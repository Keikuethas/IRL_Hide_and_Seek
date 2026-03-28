package com.keikuethas.irlhideandseek

import kotlinx.serialization.Serializable
import java.util.UUID

// Класс для хранения информации об игроке. В частности, его роль и идентификатор
@Serializable
data class Player(
    val name: String,
    var role: PlayerRole = PlayerRole.Hider,
    val id: String = UUID.randomUUID().toString()
) {
}