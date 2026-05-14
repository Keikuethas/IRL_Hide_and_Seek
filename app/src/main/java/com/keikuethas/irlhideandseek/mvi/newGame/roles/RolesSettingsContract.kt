package com.keikuethas.irlhideandseek.mvi.newGame.roles

import android.os.Parcelable
import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.Intel
import com.keikuethas.irlhideandseek.PersonalBomb
import com.keikuethas.irlhideandseek.PlayerRole
import com.keikuethas.irlhideandseek.RoleParams
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.SafeHouse
import com.keikuethas.irlhideandseek.SafeMansion
import com.keikuethas.irlhideandseek.Scan
import com.keikuethas.irlhideandseek.Shield
import com.keikuethas.irlhideandseek.Snare
import com.keikuethas.irlhideandseek.Trap
import com.keikuethas.irlhideandseek.getAbilityByType
import com.keikuethas.irlhideandseek.view.DialogInputType
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlin.reflect.KClass

@Parcelize
data class AbilityState(
    val type: @RawValue KClass<out Ability>, //concern если это будет ломаться, то сделать маппер на enum и забить
    val params: List<Pair<String, Number>>
) : Parcelable {
    constructor(ability: Ability) : this(
        type = ability::class,
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
    val health: Int = 100,
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

    val params: RoleParams
        get() = RoleParams(health, type)

    val remainingAbilities: List<KClass<out Ability>>
        get() {
            val res: MutableList<KClass<out Ability>> = mutableListOf()
            abilityList.forEach { candidate ->
                if (abilities.all { it.type != candidate })
                    res.add(candidate)
            }
            return res.toList()
        }

    val displayAbilityAdd: Boolean
        get() = remainingAbilities.isNotEmpty()

    companion object {
        val abilityList: List<KClass<out Ability>> = listOf(
            Shield::class,
            Intel::class,
            Scan::class,
            PersonalBomb::class,
            Trap::class,
            Snare::class,
            SafeHouse::class,
            SafeMansion::class
        )
    }
}

@Parcelize
data class ValueInputDialogState( // Value Input Dialog State
    val initialValue: String = "",
    val inputType: DialogInputType = DialogInputType.STRING,
    val paramName: String,
    val abilityType: @RawValue KClass<out Ability>? = null
) : Parcelable

@Parcelize
data class RSState(
    val roles: List<RoleState> = emptyList(),
    val currentRole: Int = 0,
    val showQuitDialog: Boolean = false,
    val showRoleRemoveDialog: Boolean = false,
    val showValueInputDialog: ValueInputDialogState? = null,
    val showRoleTypeDialog: Boolean = false,
    val showAbilityAddDialog: Boolean = false
) : Parcelable

sealed interface RSIntent {
    data class QuitAnswer(val result: Boolean) : RSIntent
    data object QuitRequest : RSIntent
    data object Save : RSIntent
    data object RoleNameClick : RSIntent
    data object RoleTypeClick : RSIntent
    data class RoleTypeChangeAnswer(val changed: Boolean) : RSIntent
    data class ParamClick(
        val type: KClass<out Ability>,
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
    data class AddAbility(val type: KClass<out Ability>) : RSIntent
    data object RoleHealthClick : RSIntent
    data class DeleteAbility(val type: KClass<out Ability>): RSIntent

}

sealed interface RSResult {
    data class ScrollRoles(val right: Boolean) : RSResult
    data class VIDStateChanged(val state: ValueInputDialogState?) : RSResult
    data class QuitDialogStateChanged(val open: Boolean) : RSResult
    data object RoleCreated : RSResult
    data class RoleDeleteDialogStateChanged(val open: Boolean) : RSResult
    data class RoleDeleted(val id: Int) : RSResult
    data class RoleNameChanged(val newName: String) : RSResult
    data class HealthChanged(val newValue: Int) : RSResult
    data class ParameterChanged(val name: String, val newValue: Number) : RSResult
    data object RoleTypeChanged : RSResult
    data class RoleTypeDialogStateSet(val open: Boolean) : RSResult
    data class AbilityAdded(val type: KClass<out Ability>): RSResult
    data class AddAbilityDialogStateSet(val open: Boolean): RSResult
    data class AbilityDeleted(val type: KClass<out Ability>): RSResult
}

sealed interface RSEffect {
    data object Quit : RSEffect
}