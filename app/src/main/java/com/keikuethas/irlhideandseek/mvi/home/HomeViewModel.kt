package com.keikuethas.irlhideandseek.mvi.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.mvi.home.HomeResult.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    // внутреннее изменяемое состояние
    private val _homeState = MutableStateFlow(value = HomeState())

    // внешнее свойство для чтения
    val homeState = _homeState.asStateFlow()

    // поток одноразовых событий
    private val _effect = MutableSharedFlow<HomeEffect>()

    // внешний поток для чтения
    val effect = _effect.asSharedFlow()

    // Сюда приходят все действия пользователя
    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.CreateGame -> sendEffect(HomeEffect.HostLobby)
            is HomeIntent.EditName -> dispatch(NameEdited(intent.value))
            is HomeIntent.EditRoomName -> dispatch(RoomNameEdited(intent.value))
            is HomeIntent.JoinGame -> joinGame()
            HomeIntent.GrantPermissions -> sendEffect(HomeEffect.OpenSettings)
            HomeIntent.DismissError -> dispatch(HomeResult.ErrorDismissed)
        }
    }


    private fun joinGame() {
        val gameExists = !true //todo запрос к серверу

        if (gameExists) sendEffect(HomeEffect.JoinLobby(homeState.value.roomNameText))
        else dispatch(
            Error(
                "Игра не найдена",
                "Не удалось найти игру с таким идентификатором. " +
                        "Пожалуйста, проверьте корректность ввода и повторите попытку."
            )
        )
    }

    // Сюда передаём результат, чтобы получить новое состояние
    private fun dispatch(result: HomeResult) {
        _homeState.value = HomeReducer.reduce(
            state = homeState.value,
            result = result
        )
    }

    // Отправка одноразового события в UI
    private fun sendEffect(effect: HomeEffect) =
        viewModelScope.launch { _effect.emit(value = effect) }
}