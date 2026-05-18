package com.keikuethas.irlhideandseek.mvi.lobby

import android.os.Parcelable
import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.PlayerRole
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RoleState
import kotlinx.parcelize.Parcelize

// Состояние экрана
@Parcelize
data class LobbyState(
    val roomName: String = "",
    val playerName: String = "",
    val roomCode: String = "AMOGUS", // код комнаты
    val playerRole: String = "",
    val players: List<Pair<String, String>> = emptyList(),
    val roles: List<String> = emptyList(),
    val isReady: Boolean = false,
    val showRoleChangeDialog: Boolean = false,
    val showQuitDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) : Parcelable

// Действия на экране. Запросы на изменение состояния
sealed interface LobbyIntent {
    data object QuitRequest: LobbyIntent // Пользователь нажал "покинуть"
    data class QuitDialogRespond(val confirmed: Boolean): LobbyIntent // Пользователь (не) подтвердил выход в диалоговом окне
    data object ChangeReadyStatus: LobbyIntent // Поменять статус готовности
    data object RequestRoleChangeDialog: LobbyIntent // Пользователь нажал "сменить роль"
    data class ChangeRole(val newRole: String): LobbyIntent // Пользователь хочет поменять роль
    data object DeclineRoleChange: LobbyIntent // Пользователь закрыл диалог без смены роли
}

// Результат действия. Меняет состояние
sealed interface LobbyResult {
    data object ReadyStatusChanged: LobbyResult // Статус готовности изменился на противоположный
    data class ReadyStatusSet(val ready: Boolean): LobbyResult // Установка статуса готовности
    data class PlayerJoined(val name: String, val role: String): LobbyResult // Присоединился игрок
    data class PlayerQuit(val name: String): LobbyResult // Игрок вышел
    data class RoleChanged(val name: String, val role: String): LobbyResult // У игрока изменилась роль
    data class RoleChangeDialogStateSet(val open: Boolean): LobbyResult // Диалог смены роли открыт/закрыт
    data class QuitDialogStateSet(val open: Boolean): LobbyResult // Диалог подтверждения выхода

    data class SetPlayerInfo(val playerName: String, val roomName: String) : LobbyResult
    data class InitState(
        val roomName: String,
        val playerName: String,
        val playerRole: String,
        val players: List<Pair<String, String>>,
        val roles: List<String>,
        val isReady: Boolean
    ) : LobbyResult
    data class Error(val title: String, val message: String) : LobbyResult
    // Если нужно обновить роль конкретного игрока:
    data class PlayerRoleChanged(val newRoleId: String) : LobbyResult

    data class Loading(val isLoading: Boolean) : LobbyResult
}

// Одноразовые события для UI
sealed interface LobbyEffect {
    data object Quit: LobbyEffect // Выход на главный экран
}