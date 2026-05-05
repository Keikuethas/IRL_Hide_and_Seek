package com.keikuethas.irlhideandseek.mvi.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.Intel
import com.keikuethas.irlhideandseek.PersonalBomb
import com.keikuethas.irlhideandseek.PlayerRole
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.Shield
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LobbyViewModel : ViewModel() {

    // Внутреннее изменяемое состояние
    private val _lobbyState = MutableStateFlow(value = LobbyState())

    // Внешний поток для чтения
    val lobbyState = _lobbyState.asStateFlow()

    // Внутренний поток изменяемых событий
    private val _effect = MutableSharedFlow<LobbyEffect>()

    // Внешний поток событий для чтения
    val effect = _effect.asSharedFlow()

    init {
        // Server получение ролей с сервера
        val roleList = listOf(
            PlayerRole("Amogus", abilities = listOf(Shield(), Intel()), type = RoleType.Hider),
            PlayerRole("Zlobus", abilities = listOf(PersonalBomb()), type = RoleType.Seeker)
        )// TEMP

        // Server получение игроков с сервера
        val playerList = listOf(
            "Keikuethas" to "Amogus",
            "Keopint" to "Zlobus"
        ) // TEMP

        _lobbyState.value = _lobbyState.value.copy(
            players = playerList,
            roles = roleList
        )
    }

    //todo Server обновление списка игроков по сообщениям сервера

    fun onIntent(intent: LobbyIntent) {
        when (intent) {
            LobbyIntent.ChangeReadyStatus -> dispatch(LobbyResult.ReadyStatusChanged)

            is LobbyIntent.ChangeRole -> changeUserRole(intent.newRole)

            LobbyIntent.DeclineRoleChange ->
                dispatch(LobbyResult.RoleChangeDialogStateSet(false))

            is LobbyIntent.QuitDialogRespond -> if (intent.confirmed) sendEffect(LobbyEffect.Quit)
            else dispatch(LobbyResult.QuitDialogStateSet(false))

            LobbyIntent.QuitRequest -> dispatch(LobbyResult.QuitDialogStateSet(true))

            LobbyIntent.RequestRoleChangeDialog ->
                dispatch(LobbyResult.RoleChangeDialogStateSet(true))
        }
    }

    // Изменение роли в UI
    private fun changeUserRole(role: String) {
        viewModelScope.launch { sendChangeRole(role) } //concern coroutine scope
        dispatch(LobbyResult.RoleChanged(lobbyState.value.playerName, role))
        dispatch(LobbyResult.RoleChangeDialogStateSet(false))
    }

    // Server отправка сообщения об изменении роли пользователя
    private suspend fun sendChangeRole(role: String) {
        //TODO
    }

    // Server по сообщению с сервера изменить роль
    private fun changeRole(name: String, role: String) {
        dispatch(LobbyResult.RoleChanged(name, role))
    }

    private fun dispatch(result: LobbyResult) {
        _lobbyState.value = LobbyReducer.reduce(
            state = lobbyState.value,
            result = result
        )
    }

    private fun sendEffect(effect: LobbyEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

}