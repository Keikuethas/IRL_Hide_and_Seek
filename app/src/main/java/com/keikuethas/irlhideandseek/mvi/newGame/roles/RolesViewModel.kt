package com.keikuethas.irlhideandseek.mvi.newGame.roles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.data.repository.NewGameRepository
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.view.DialogInputType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.InvalidParameterException
import javax.inject.Inject

@HiltViewModel
class RolesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NewGameRepository // Инъекция репозитория
) : MVI_HiltViewModel<RSState, RSIntent, RSEffect, RSResult>(
    initialState = RSState(),
    savedStateKey = "RolesSettings",
    savedStateHandle = savedStateHandle
) {

    init {
        // Загружаем текущие настройки из репозитория при открытии экрана
        viewModelScope.launch {
            val initialRoles = repository.newGameState.value.rolesSettings
            onIntent(RSIntent.Initialize(initialRoles))
        }
    }

    override fun onIntent(intent: RSIntent) {
        when (intent) {
            is RSIntent.ArrowClick -> dispatch(RSResult.ScrollRoles(intent.right))

            is RSIntent.ParamClick ->
                dispatch(
                    RSResult.VIDStateChanged(
                        AbilityVIDState(
                            paramName = intent.name,
                            initialValue = state.value.run {
                                roles[currentRole].abilities
                                    .find { it.type == intent.type }?.params
                                    ?.find { it.first == intent.name }?.second
                            }!!.toString(),
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
                dispatch(RSResult.QuitDialogStateChanged(false))

            RSIntent.QuitRequest ->
                dispatch(RSResult.QuitDialogStateChanged(true))

            RSIntent.RoleCreate -> dispatch(RSResult.RoleCreated)

            is RSIntent.RoleDeleteAnswer -> {
                if (intent.result)
                    dispatch(RSResult.RoleDeleted(state.value.currentRole))
                dispatch(RSResult.RoleDeleteDialogStateChanged(false))
            }

            RSIntent.RoleNameClick -> dispatch(
                RSResult.VIDStateChanged(
                    AbilityVIDState(
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
                // Сохраняем текущее состояние в репозиторий
                repository.updateRolesSettings(state.value)
                sendEffect(RSEffect.Save)
            }

            RSIntent.ValueChangeDismiss ->
                dispatch(RSResult.VIDStateChanged(null))

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
                dispatch(RSResult.VIDStateChanged(null))
            }


            RSIntent.RoleDeleteRequest ->
                dispatch(RSResult.RoleDeleteDialogStateChanged(true))

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
                RSResult.VIDStateChanged(
                    AbilityVIDState(
                        paramName = "health",
                        initialValue = state.value.run {
                            roles[currentRole].health.toString()
                        },
                        inputType = DialogInputType.INT
                    )
                )
            )

            is RSIntent.DeleteAbility -> dispatch(RSResult.AbilityDeleted(intent.type))
            is RSIntent.Initialize -> dispatch(RSResult.Initialized(intent.state))
        }
    }

    override fun reduce(
        state: RSState,
        result: RSResult
    ): RSState = RolesReducer.reduce(state, result)
}