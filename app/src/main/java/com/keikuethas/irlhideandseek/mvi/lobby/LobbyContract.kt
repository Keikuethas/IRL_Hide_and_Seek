package com.keikuethas.irlhideandseek.mvi.lobby

import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.PlayerRole

// Состояние экрана
data class LobbyState(
    val roomName: String = "", // Название комнаты
    val playerName: String = "", // Имя игрока (пользователя)
    val playerRole: String = "", // Роль игрока (пользователя)
    val players: List<Pair<String, String>> = listOf(), // Игроки в формате "Имя, роль"
    val roles: List<PlayerRole> = listOf(), // Список доступных ролей
    val isReady: Boolean = false, // Готов ли пользователь
    val showRoleChangeDialog: Boolean = false,
    val showQuitDialog: Boolean = false
)

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
}

// Одноразовые события для UI
sealed interface LobbyEffect {
    data object Quit: LobbyEffect // Выход на главный экран
}