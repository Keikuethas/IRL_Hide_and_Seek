package com.keikuethas.irlhideandseek.mvi.lobby

import android.os.Parcelable
import com.keikuethas.irlhideandseek.websocket.incoming.PlayerInfo
import com.keikuethas.irlhideandseek.websocket.incoming.RoleInfo
import kotlinx.parcelize.Parcelize

// Состояние экрана
@Parcelize
data class LobbyState(
    val roomName: String = "",
    val playerName: String = "",
    val roomCode: String = "AMOGUS", // код комнаты
    val playerRole: String = "", // ИМЯ роли
    val players: List<PlayerInfo> = emptyList(),
    val roles: List<RoleInfo> = emptyList(),
    val isReady: Boolean = false,
    val showRoleChangeDialog: Boolean = false,
    val showQuitDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) : Parcelable

// Действия на экране. Запросы на изменение состояния
sealed interface LobbyIntent {
    data object QuitRequest : LobbyIntent // Пользователь нажал "покинуть"
    data class QuitDialogRespond(val confirmed: Boolean) :
        LobbyIntent // Пользователь (не) подтвердил выход в диалоговом окне

    data object ChangeReadyStatus : LobbyIntent // Поменять статус готовности
    data object RequestRoleChangeDialog : LobbyIntent // Пользователь нажал "сменить роль"
    data class ChangeRole(val roleId: String) : LobbyIntent // Пользователь хочет поменять роль
    data object DeclineRoleChange : LobbyIntent // Пользователь закрыл диалог без смены роли
    data object DismissError : LobbyIntent
}

// Результат действия. Меняет состояние
sealed interface LobbyResult {
    data object ReadyStatusChanged : LobbyResult // Статус готовности изменился на противоположный
    data class PlayerReadyStatusSet(val playerId: String, val ready: Boolean): LobbyResult
    data class ReadyStatusSet(val ready: Boolean) : LobbyResult // Установка статуса готовности
    data class PlayerJoined(val id: String, val playerName: String, val roleId: String) : LobbyResult // Присоединился игрок
    data class PlayerQuit(val id: String) : LobbyResult // Игрок вышел
    data class RoleChanged(val role: String) :
        LobbyResult // У текущего игрока изменилась роль

    data class RoleChangeDialogStateSet(val open: Boolean) :
        LobbyResult // Диалог смены роли открыт/закрыт

    data class QuitDialogStateSet(val open: Boolean) : LobbyResult // Диалог подтверждения выхода

    data class SetPlayerInfo(val playerName: String, val roomName: String) : LobbyResult
    data class InitState(
        val roomName: String,
        val playerName: String,
        val playerRole: String,
        val players: List<PlayerInfo>,
        val roles: List<RoleInfo>,
        val isReady: Boolean,
        val roomCode: String
    ) : LobbyResult

    data class Error(val title: String, val message: String) : LobbyResult

    // Если нужно обновить роль конкретного игрока:
    data class PlayerRoleChanged(val id: String, val newRoleId: String) : LobbyResult

    data class Loading(val isLoading: Boolean) : LobbyResult
}

// Одноразовые события для UI
sealed interface LobbyEffect {
    data object Quit : LobbyEffect // Выход на главный экран
    data class StartGame(val timeToHide: Int): LobbyEffect
}