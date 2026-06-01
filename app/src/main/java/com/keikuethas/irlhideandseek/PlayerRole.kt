package com.keikuethas.irlhideandseek

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
        when (this) {
            HIDER -> "Задача игроков с ролью этого типа - оставаться в живых, пока не истечёт время игры."
            SEEKER -> "Задача игроков с ролью этого типа - найти всех прячущихся до истечения времени игры."
        }
    }
}