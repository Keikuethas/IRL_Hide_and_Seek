package com.keikuethas.irlhideandseek.mvi.newGame.events

import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.data.NewGameRepository
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.view.components.DialogInputType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.InvalidParameterException
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NewGameRepository
) : MVI_HiltViewModel<ESState, ESIntent, ESEffect, ESResult>(
    initialState = ESState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "EventsSettings"
) {

    init {
        // Загружаем текущие настройки из репозитория при открытии экрана
        viewModelScope.launch {
            val initialEvents = repository.newGameState.value.eventSettings
            onIntent(ESIntent.Initialize(initialEvents))
        }
    }

    override fun onIntent(intent: ESIntent) = when (intent) {
        is ESIntent.AddAbility -> {
            dispatch(ESResult.EventAdded(intent.type))
            if (state.value.unsetEvents.isEmpty())
                dispatch(ESResult.AddEventDialogStateSet(false)) else {}
        }
        ESIntent.AddEventDismissed -> dispatch(ESResult.AddEventDialogStateSet(false))
        is ESIntent.DeleteEvent -> dispatch(ESResult.EventDeleted(intent.type))
        is ESIntent.QuitAnswer -> if (intent.confirmed) sendEffect(ESEffect.Quit)
        else dispatch(ESResult.QuitDialogStateChanged(false))

        ESIntent.RequestAddEvent -> dispatch(ESResult.AddEventDialogStateSet(true))
        ESIntent.RequestQuit -> dispatch(ESResult.QuitDialogStateChanged(true))

        is ESIntent.RequestValueChange -> dispatch(
            ESResult.VIDStateChanged(
                EventVIDState(
                    initialValue = state.value.events.find { it.type == intent.type }!!.params
                        .find { it.first == intent.name }!!.second.toString(),
                    inputType = when (intent.name) {
                        "duration_seconds" -> DialogInputType.INT
                        "radius" -> DialogInputType.FLOAT
                        "damage" -> DialogInputType.INT
                        else -> DialogInputType.STRING
                    },
                    paramName = intent.name,
                    eventType = intent.type
                )
            )
        )

        ESIntent.ResetSettings -> dispatch(ESResult.EventsCleared)

        ESIntent.SaveSettings -> {
            // Сохраняем текущее состояние в репозиторий
            repository.updateEventsSettings(state.value)
            sendEffect(ESEffect.Save)
        }

        ESIntent.ValueChangeDismiss -> dispatch(ESResult.VIDStateChanged(null))

        is ESIntent.ValueChanged -> {
            dispatch(
                ESResult.ParameterChanged(
                    state.value.showValueInputDialog!!.paramName,
                    when (state.value.showValueInputDialog!!.inputType) {
                        DialogInputType.INT -> intent.newValue.toInt().fastCoerceAtLeast(1)
                        DialogInputType.FLOAT -> intent.newValue.toFloat().fastCoerceAtLeast(1f)
                        else -> throw InvalidParameterException("${state.value.showValueInputDialog!!.paramName} was set to ${intent.newValue}")
                    }
                )
            )
            dispatch(ESResult.VIDStateChanged(null))
        }

        is ESIntent.ChangeFrequency -> with(intent) {
            dispatch(
                ESResult.FrequencyChanged(
                    eventType = type,
                    newValue = newValue
                )
            )
        }

        is ESIntent.Initialize -> dispatch(ESResult.Initialized(intent.newState))
    }

    override fun reduce(state: ESState, result: ESResult) =
        EventsReducer.reduce(state, result)
}