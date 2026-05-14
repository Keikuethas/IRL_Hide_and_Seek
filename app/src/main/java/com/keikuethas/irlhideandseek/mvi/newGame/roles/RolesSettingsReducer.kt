package com.keikuethas.irlhideandseek.mvi.newGame.roles

import android.util.Log
import com.keikuethas.irlhideandseek.PlayerRole
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.getAbilityByType

object RolesSettingsReducer {
    fun reduce(state: RSState, result: RSResult): RSState =
        when (result) {

            is RSResult.AbilityAdded -> {
                val newAbilityList = state.roles[state.currentRole].abilities +
                        AbilityState(getAbilityByType(result.type))
                val newRole = state.roles[state.currentRole].copy(
                    abilities = newAbilityList
                )
                val newRoleList: List<RoleState> = state.roles.subList(0, state.currentRole) +
                        newRole +
                        state.roles.subList(state.currentRole + 1, state.roles.size)
                state.copy(
                    roles = newRoleList
                )
            }

            is RSResult.AddAbilityDialogStateSet -> state.copy(showAbilityAddDialog = result.open)

            is RSResult.HealthChanged -> {
                val newRole = state.roles[state.currentRole].copy(
                    health = result.newValue
                )
                val newRoleList: List<RoleState> = state.roles.subList(0, state.currentRole) +
                        newRole +
                        state.roles.subList(state.currentRole + 1, state.roles.size)
                state.copy(
                    roles = newRoleList
                )
            }

            is RSResult.ParameterChanged -> {

                val abilityType = state.showValueInputDialog!!.abilityType!!

                val oldParams = state.roles[state.currentRole].abilities
                    .find { it.type == abilityType }!!.params

                val newParams: MutableMap<String, Number> = mutableMapOf()

                oldParams.forEach {
                    if (it.first == result.name) newParams[result.name] = result.newValue
                    else newParams[it.first] = it.second
                }

                val newAbility = AbilityState(getAbilityByType(abilityType, newParams))

                val newAbilityList: MutableList<AbilityState> = mutableListOf()
                state.roles[state.currentRole].abilities.forEach {
                    if (it.type == abilityType) newAbilityList.add(newAbility)
                    else newAbilityList.add(it)
                }

                val newRole = state.roles[state.currentRole].copy(
                    abilities = newAbilityList
                )

                val newRoleList: List<RoleState> = state.roles.subList(0, state.currentRole) +
                        newRole +
                        state.roles.subList(state.currentRole + 1, state.roles.size)

                state.copy(roles = newRoleList)
            }

            is RSResult.QuitDialogStateChanged -> state.copy(showQuitDialog = result.open)

            RSResult.RoleCreated -> {
                val newRole = RoleState(PlayerRole("New role", type = RoleType.Hider), 100)
                val newRoleList: List<RoleState> = state.roles + newRole
                Log.i("susdebug", "still alive")
                state.copy(roles = newRoleList)
            }

            is RSResult.RoleDeleteDialogStateChanged -> state.copy(showRoleRemoveDialog = result.open)

            is RSResult.RoleDeleted -> {
                state.copy(
                    roles = state.roles.subList(0, state.currentRole) +
                            state.roles.subList(state.currentRole + 1, state.roles.size)
                )
            }

            is RSResult.RoleNameChanged -> {
                val newRole = state.roles[state.currentRole].copy(
                    roleName = result.newName
                )
                val newRoleList: List<RoleState> = state.roles.subList(0, state.currentRole) +
                        newRole +
                        state.roles.subList(state.currentRole + 1, state.roles.size)
                state.copy(
                    roles = newRoleList
                )
            }

            RSResult.RoleTypeChanged -> {
                val oldType = state.roles[state.currentRole].type
                val newRole = state.roles[state.currentRole].copy(
                    type = if (oldType == RoleType.Hider) RoleType.Seeker else RoleType.Hider
                )
                val newRoleList: List<RoleState> = state.roles.subList(0, state.currentRole) +
                        newRole +
                        state.roles.subList(state.currentRole + 1, state.roles.size)
                state.copy(
                    roles = newRoleList
                )
            }

            is RSResult.RoleTypeDialogStateSet -> state.copy(showRoleTypeDialog = result.open)

            is RSResult.ScrollRoles -> {
                var newValue = state.currentRole + if (result.right) 1 else -1
                when {
                    newValue > state.roles.size -> newValue = 0
                    newValue < 0 -> newValue = state.roles.size
                }
                state.copy(
                    currentRole = newValue
                )
            }

            is RSResult.VIDStateChanged -> state.copy(
                showValueInputDialog = result.state
            )
        }
}