package com.keikuethas.irlhideandseek.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerRole(
    val name: String,
    val abilities: List<Ability> = listOf(),
    val type: RoleType
) {

}

enum class RoleType {
    HIDER, SEEKER;

    // resource
    override fun toString(): String = when(this) {
        HIDER -> "Прячущийся"
        SEEKER -> "Охотник"
    }

    // resource
    val description: String by lazy {
        "Задача игроков с ролью этого типа - " + when (this) {
            HIDER -> "оставаться в живых, пока не истечёт время игры."
            SEEKER -> "найти всех прячущихся до истечения времени игры."
        }
    }
}