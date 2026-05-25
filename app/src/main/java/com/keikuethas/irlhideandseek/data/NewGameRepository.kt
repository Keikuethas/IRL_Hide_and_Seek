package com.keikuethas.irlhideandseek.data.repository

import com.keikuethas.irlhideandseek.mvi.newGame.events.ESState
import com.keikuethas.irlhideandseek.mvi.newGame.main.MSState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewGameRepository @Inject constructor() {

    // Состояние создания игры (общее для всех экранов)
    private val _newGameState = MutableStateFlow(NewGameSessionState())
    val newGameState: StateFlow<NewGameSessionState> = _newGameState.asStateFlow()

    // Обновление имени комнаты
    fun updateRoomName(name: String) {
        _newGameState.value = _newGameState.value.copy(roomName = name)
    }

    // Обновление настроек ролей
    fun updateRolesSettings(rolesState: RSState) {
        _newGameState.value = _newGameState.value.copy(rolesSettings = rolesState)
    }

    // Обновление настроек событий
    fun updateEventsSettings(eventsState: ESState) {
        _newGameState.value = _newGameState.value.copy(eventSettings = eventsState)
    }

    // Обновление настроек карты
    fun updateMapSettings(mapState: MSState) {
        _newGameState.value = _newGameState.value.copy(mapSettings = mapState)
    }

    // Сброс всех настроек
    fun resetAll() {
        _newGameState.value = NewGameSessionState()
    }

    // Получить текущее состояние
    fun getCurrentState(): NewGameSessionState = _newGameState.value
}

// Состояние сессии создания игры
data class NewGameSessionState(
    val roomName: String = "New game",
    val selectedPreset: String = "<new>",
    val rolesSettings: RSState = RSState(),
    val eventSettings: ESState = ESState(),
    val mapSettings: MSState = MSState() // создайте заглушку если нет
)