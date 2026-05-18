package com.keikuethas.irlhideandseek.mvi.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeError(
    val title: String,
    val description: String
) : Parcelable

// Состояние экрана
@Parcelize
data class HomeState(
    val nameText: String = "", // Текст в поле ввода имени
    val roomNameText: String = "", // Текст в поле ввода ID комнаты
    val error: HomeError? = null, // Возникшая ошибка
    val isLoading: Boolean = false,
    val isLocationPermissionGranted: Boolean = false // ✅ Новое поле
) : Parcelable {
    val nameTextCounter get() = "${nameText.length}/$MAXNAMELENGTH"
    val buttonsActive get() = nameText.isNotBlank() && isLocationPermissionGranted // ✅ Кнопки активны только с разрешением

    companion object {
        const val MAXNAMELENGTH: Int = 20
    }
}

// Действия пользователя на экране
sealed interface HomeIntent {
    data object CreateGame : HomeIntent
    data class EditName(val value: String) : HomeIntent
    data class EditRoomName(val value: String) : HomeIntent
    data object GrantPermissions : HomeIntent // ✅ Оставим для открытия настроек (фолбэк)
    data object RequestLocationPermission : HomeIntent // ✅ Для системного диалога
    data class PermissionResult(val granted: Boolean) : HomeIntent // ✅ Результат системного диалога
    data object DismissError : HomeIntent
}

// Результаты обработки действий
sealed interface HomeResult {
    data class NameEdited(val value: String) : HomeResult // Имя обновилось
    data class RoomNameEdited(val value: String) : HomeResult // Имя комнаты обновилось
    data class Error(val title: String, val description: String) : HomeResult // Возникла ошибка
    data object ErrorDismissed : HomeResult // Ошибка закрыта

    data class Loading(val isLoading: Boolean) : HomeResult
    data object PermissionGranted : HomeResult // ✅
    data object PermissionDenied : HomeResult // ✅
}

// Одноразовые события для UI
sealed interface HomeEffect {
    data object OpenSettings : HomeEffect
    data object HostLobby : HomeEffect
    data class JoinLobby(
        val playerName: String,
        val roomName: String,
        val gameId: String,
        val playerId: String
    ) : HomeEffect
}