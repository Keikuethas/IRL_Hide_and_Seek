package com.keikuethas.irlhideandseek

import kotlinx.serialization.Serializable
import java.util.UUID

// Класс для хранения информации об игроке. В частности, его роль и идентификатор
@Serializable
data class Player(
    val name: String,
    var role: PlayerRole = PlayerRole.Hider,
    var health: Int = 100,
    var is_alive: Boolean = true,
    //TODO: location
    //CONCERN: last_location_update

    val id: String = UUID.randomUUID().toString()
)