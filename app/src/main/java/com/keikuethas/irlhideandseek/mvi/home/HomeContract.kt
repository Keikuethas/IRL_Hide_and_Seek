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
    val error: HomeError? = null // Возникшая ошибка
) : Parcelable {
    val nameTextCounter get() = "${nameText.length}/$nameLengthLimit" // набрано/допустимо
    val roomNameTextCounter
        get() =
            "${roomNameText.length}/$roomNameLengthLimit" // набрано/допустимо
    val buttonsActive get() = nameText.isNotBlank() && roomNameText.isNotBlank()

    companion object {
        val nameLengthLimit: Int = 20 // макс. длина имени
        val roomNameLengthLimit: Int = 10 // макс. длина имени комнаты
    }
}

// Действия пользователя на экране
sealed interface HomeIntent {
    data object JoinGame : HomeIntent // Пользователь нажал "Присоединиться"
    data object CreateGame : HomeIntent // Пользователь нажал "Создать"
    data class EditName(val value: String) : HomeIntent // Пользователь изменил имя
    data class EditRoomName(val value: String) : HomeIntent // Пользователь изменил ID комнаты
    data object GrantPermissions : HomeIntent  // Пользователь нажал "Предоставить разрешение"
    data object DismissError : HomeIntent // Пользователь закрыл ошибку
}

// Результаты обработки действий
sealed interface HomeResult {
    data class NameEdited(val value: String) : HomeResult // Имя обновилось
    data class RoomNameEdited(val value: String) : HomeResult // Имя комнаты обновилось
    data class Error(val title: String, val description: String) : HomeResult // Возникла ошибка
    data object ErrorDismissed : HomeResult // Ошибка закрыта
}

// Одноразовые события для UI
sealed interface HomeEffect {
    data object OpenSettings : HomeEffect // Открыть настройки приложения
    data object HostLobby : HomeEffect // Перейти в лобби (создание игры)
    data class JoinLobby(val id: String) : HomeEffect // Перейти в лобби (подключиться к игре)
}