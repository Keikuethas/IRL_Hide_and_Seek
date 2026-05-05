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
    Hider, Seeker;

    // resource
    override fun toString(): String = when(this) {
        Hider -> "Прячущийся"
        Seeker -> "Охотник"
    }

    // resource
    val description: String by lazy {
        when (this) {
            Hider -> "Задача игроков с ролью этого типа - оставаться в живых, пока не истечёт время игры."
            Seeker -> "Задача игроков с ролью этого типа - найти всех прячущихся до истечения времени игры."
        }
    }
}