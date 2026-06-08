package com.keikuethas.irlhideandseek.mvi.newGame.roles

import android.os.Parcelable
import com.keikuethas.irlhideandseek.model.Ability
import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.model.PlayerRole
import com.keikuethas.irlhideandseek.model.RoleType
import com.keikuethas.irlhideandseek.model.getAbilityByType
import com.keikuethas.irlhideandseek.view.components.DialogInputType
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AbilityState(
    val type: AbilityType,
    val params: List<Pair<String, Number>>,
    val cooldownEndTime: Long = 0,
    val remainingTime: Long = 0
) : Parcelable {

    val rechargeTime: Int
        get() = params.find {
            it.first == "recharge_time"
        }!!.second.toInt()

    val cooldownProgress: Float
        get() =
            1F - (remainingTime.toFloat() / rechargeTime)

    constructor(ability: Ability) : this(
        type = ability.abilityType,
        params = ability.run {
            val res: MutableList<Pair<String, Number>> = mutableListOf()
            ability.let {
                res.addAll(
                    listOf(
                        "duration_seconds" to duration_seconds,
                        "number_uses" to number_uses,
                        "recharge_time" to recharge_time
                    )
                )
            }

            forEachParam { name, value -> res.add(name to value) }
            res
        }
    )

    val ability: Ability
        get() {
            val paramMap: MutableMap<String, Number> = mutableMapOf()
            params.forEach { paramMap[it.first] = it.second }
            return getAbilityByType(type, paramMap)
        }
}

@Parcelize
data class RoleState(
    val roleName: String = "New role",
    val type: RoleType,
    val abilities: List<AbilityState> = emptyList(),
    val health: Int = 100
) : Parcelable {
    constructor(role: PlayerRole, health: Int) : this(
        roleName = role.name,
        type = role.type,
        abilities = role.abilities.run {
            val res: MutableList<AbilityState> = mutableListOf()
            forEach { ability: Ability -> res.add(AbilityState(ability)) }
            res
        },
        health = health
    )

    val role: PlayerRole
        get() {
            val abils: MutableList<Ability> = mutableListOf()
            abilities.forEach { state: AbilityState -> abils.add(state.ability) }
            return PlayerRole(roleName, abils, type)
        }

    val remainingAbilities: List<AbilityType>
        get() = AbilityType.entries - abilities.map { it.type }.toSet()

    val displayAbilityAdd: Boolean
        get() = remainingAbilities.isNotEmpty()
}

@Parcelize
data class AbilityVIDState( // Value Input Dialog State
    val initialValue: String = "",
    val inputType: DialogInputType = DialogInputType.STRING,
    val paramName: String,
    val abilityType: @RawValue AbilityType? = null
) : Parcelable

@Parcelize
data class RSState(
    val roles: List<RoleState> = emptyList(),
    val currentRole: Int = 0,
    val showQuitDialog: Boolean = false,
    val showRoleRemoveDialog: Boolean = false,
    val showValueInputDialog: AbilityVIDState? = null,
    val showRoleTypeDialog: Boolean = false,
    val showAbilityAddDialog: Boolean = false,
) : Parcelable

sealed interface RSIntent {
    data class QuitAnswer(val result: Boolean) : RSIntent
    data object QuitRequest : RSIntent
    data object Save : RSIntent
    data object RoleNameClick : RSIntent
    data object RoleTypeClick : RSIntent
    data class RoleTypeChangeAnswer(val changed: Boolean) : RSIntent
    data class ParamClick(
        val type: AbilityType,
        val name: String
    ) : RSIntent

    data object RoleCreate : RSIntent
    data class ArrowClick(val right: Boolean) : RSIntent
    data object ValueChangeDismiss : RSIntent
    data class ValueChanged(val newValue: String) : RSIntent
    data object RoleDeleteRequest : RSIntent
    data class RoleDeleteAnswer(val result: Boolean) : RSIntent
    data object AddAbilityRequest : RSIntent
    data object AddAbilityDismissed : RSIntent
    data class AddAbility(val type: AbilityType) : RSIntent
    data object RoleHealthClick : RSIntent
    data class DeleteAbility(val type: AbilityType) : RSIntent

    data class Initialize(val state: RSState) : RSIntent

}

sealed interface RSResult {
    data class ScrollRoles(val right: Boolean) : RSResult
    data class VIDStateChanged(val state: AbilityVIDState?) : RSResult
    data class QuitDialogStateChanged(val open: Boolean) : RSResult
    data object RoleCreated : RSResult
    data class RoleDeleteDialogStateChanged(val open: Boolean) : RSResult
    data class RoleDeleted(val id: Int) : RSResult
    data class RoleNameChanged(val newName: String) : RSResult
    data class HealthChanged(val newValue: Int) : RSResult
    data class ParameterChanged(val name: String, val newValue: Number) : RSResult
    data object RoleTypeChanged : RSResult
    data class RoleTypeDialogStateSet(val open: Boolean) : RSResult
    data class AbilityAdded(val type: AbilityType) : RSResult
    data class AddAbilityDialogStateSet(val open: Boolean) : RSResult
    data class AbilityDeleted(val type: AbilityType) : RSResult
    data class Initialized(val state: RSState) : RSResult
}

sealed interface RSEffect {
    data object Quit : RSEffect
    data object Save : RSEffect
}