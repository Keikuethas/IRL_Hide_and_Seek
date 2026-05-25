package com.keikuethas.irlhideandseek.mvi.newGame.events

import android.os.Parcelable
import com.keikuethas.irlhideandseek.FrequencyType
import com.keikuethas.irlhideandseek.Websocket.EventType
import com.keikuethas.irlhideandseek.view.DialogInputType
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventState(
    val type: EventType,
    val frequency: FrequencyType = FrequencyType.FREQUENT,
    val params: List<Pair<String, Number>> = when (type) {
        EventType.REVEAL -> emptyList()
        EventType.BOMB -> listOf(
            "duration_seconds" to 600,
            "radius" to 10F,
            "damage" to 100
        )

        EventType.AIRDROP -> listOf(
            "radius" to 10F,
        )

        EventType.BOMBARDMENT -> listOf(
            "duration_seconds" to 600,
            "radius" to 5F,
            "damage" to 50
        )
    }
) : Parcelable

@Parcelize
data class EventVIDState( // Value Input Dialog State
    val initialValue: String = "",
    val inputType: DialogInputType = DialogInputType.STRING,
    val paramName: String,
    val eventType: EventType
) : Parcelable

@Parcelize
data class ESState(
    val events: List<EventState> = emptyList(),

    val showQuitDialog: Boolean = false,
    val showValueInputDialog: EventVIDState? = null,
    val showEventAddDialog: Boolean = false
) : Parcelable {
    val unsetEvents: List<EventType>
        get() = EventType.entries - events.map { it.type }.toSet()

    val displayAddEvent: Boolean get() = unsetEvents.isNotEmpty()
}

sealed interface ESIntent {
    data class QuitAnswer(val confirmed: Boolean) : ESIntent
    data object ValueChangeDismiss : ESIntent
    data class ValueChanged(val newValue: String) : ESIntent
    data object AddEventDismissed : ESIntent
    data class AddAbility(val type: EventType) : ESIntent
    data class DeleteEvent(val type: EventType) : ESIntent
    data object RequestAddEvent : ESIntent
    data class RequestValueChange(val type: EventType, val name: String) : ESIntent
    data object SaveSettings : ESIntent
    data object ResetSettings : ESIntent
    data object RequestQuit : ESIntent
    data class ChangeFrequency(val type: EventType, val newValue: FrequencyType): ESIntent

    data class Initialize(val newState: ESState): ESIntent
}

sealed interface ESResult {
    data class VIDStateChanged(val state: EventVIDState?) : ESResult
    data class QuitDialogStateChanged(val open: Boolean) : ESResult
    data class ParameterChanged(val name: String, val newValue: Number) : ESResult
    data class EventAdded(val type: EventType) : ESResult
    data class AddEventDialogStateSet(val open: Boolean) : ESResult
    data class EventDeleted(val type: EventType) : ESResult
    data object EventsCleared: ESResult
    data class FrequencyChanged(val eventType: EventType, val newValue: FrequencyType): ESResult
    data class Initialized(val newState: ESState): ESResult
}

sealed interface ESEffect {
    data object Quit : ESEffect
    data object Save: ESEffect
}

