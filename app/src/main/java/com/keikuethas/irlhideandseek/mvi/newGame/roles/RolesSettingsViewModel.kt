package com.keikuethas.irlhideandseek.mvi.newGame.roles

import androidx.lifecycle.SavedStateHandle
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSResult.QuitDialogStateChanged
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSResult.RoleCreated
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSResult.RoleDeleteDialogStateChanged
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSResult.RoleDeleted
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSResult.ScrollRoles
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSResult.VIDStateChanged
import com.keikuethas.irlhideandseek.view.DialogInputType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.security.InvalidParameterException
import javax.inject.Inject

@HiltViewModel
class RolesSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : MVI_HiltViewModel<RSState, RSIntent, RSEffect, RSResult>(
    initialState = RSState(),
    savedStateKey = "RolesSettings",
    savedStateHandle = savedStateHandle
) {
    override fun onIntent(intent: RSIntent) {
        when (intent) {
            is RSIntent.ArrowClick -> dispatch(ScrollRoles(intent.right))

            is RSIntent.ParamClick ->
                dispatch(
                    VIDStateChanged(
                        ValueInputDialogState(
                            paramName = intent.name,
                            initialValue = state.value.run {
                                roles[currentRole].abilities
                                    .find { it.type == intent.type }?.params
                                    ?.find { it.first == intent.name }?.second
                            }!!.toString(), // NOTE: уверенность 100%
                            inputType = when (intent.name) {
                                "duration_seconds" -> DialogInputType.INT
                                "number_uses" -> DialogInputType.INT
                                "recharge_time" -> DialogInputType.INT
                                "radius" -> DialogInputType.FLOAT
                                "trap_duration_seconds" -> DialogInputType.INT
                                "damage" -> DialogInputType.INT
                                else -> DialogInputType.STRING
                            },
                            abilityType = intent.type
                        )
                    )
                )

            is RSIntent.QuitAnswer -> if (intent.result)
                sendEffect(RSEffect.Quit)
            else
                dispatch(QuitDialogStateChanged(false))

            RSIntent.QuitRequest ->
                dispatch(QuitDialogStateChanged(true))

            RSIntent.RoleCreate -> dispatch(RoleCreated)

            is RSIntent.RoleDeleteAnswer -> {
                if (intent.result)
                    dispatch(RoleDeleted(state.value.currentRole))
                dispatch(RoleDeleteDialogStateChanged(false))
            }

            RSIntent.RoleNameClick -> dispatch(
                VIDStateChanged(
                    ValueInputDialogState(
                        paramName = "roleName",
                        initialValue = state.value.run {
                            roles[currentRole].roleName
                        },
                        inputType = DialogInputType.STRING
                    )
                )
            )

            RSIntent.RoleTypeClick ->
                dispatch(RSResult.RoleTypeDialogStateSet(true))

            RSIntent.Save -> {
                //TODO: сохранение в головную вьюмодель
                sendEffect(RSEffect.Quit)
            }

            RSIntent.ValueChangeDismiss ->
                dispatch(VIDStateChanged(null))

            is RSIntent.ValueChanged -> {
                val paramName = state.value.showValueInputDialog!!.paramName
                val type = state.value.showValueInputDialog!!.inputType
                when (paramName) {
                    "roleName" -> dispatch(RSResult.RoleNameChanged(intent.newValue))
                    "health" ->
                        dispatch(RSResult.HealthChanged(intent.newValue.toInt().coerceIn(1, 999)))

                    else -> dispatch(
                        RSResult.ParameterChanged(
                            paramName, when (type) {
                                DialogInputType.INT -> intent.newValue.toInt()
                                DialogInputType.FLOAT -> intent.newValue.toFloat()
                                else -> throw InvalidParameterException("Not Number value: ${intent.newValue} for parameter $paramName")
                            }
                        )
                    )
                }
                dispatch(VIDStateChanged(null))
            }


            RSIntent.RoleDeleteRequest ->
                dispatch(RoleDeleteDialogStateChanged(true))

            is RSIntent.RoleTypeChangeAnswer -> {
                if (intent.changed)
                    dispatch(RSResult.RoleTypeChanged)
                dispatch(RSResult.RoleTypeDialogStateSet(false))
            }

            is RSIntent.AddAbility -> {
                dispatch(RSResult.AbilityAdded(intent.type))
                dispatch(RSResult.AddAbilityDialogStateSet(false))
            }

            RSIntent.AddAbilityDismissed ->
                dispatch(RSResult.AddAbilityDialogStateSet(false))

            RSIntent.AddAbilityRequest ->
                dispatch(RSResult.AddAbilityDialogStateSet(true))

            RSIntent.RoleHealthClick -> dispatch(
                VIDStateChanged(
                    ValueInputDialogState(
                        paramName = "health",
                        initialValue = state.value.run {
                            roles[currentRole].health.toString()
                        },
                        inputType = DialogInputType.INT
                    )
                )
            )
        }
    }

    override fun reduce(
        state: RSState,
        result: RSResult
    ): RSState = RolesSettingsReducer.reduce(state, result)

}