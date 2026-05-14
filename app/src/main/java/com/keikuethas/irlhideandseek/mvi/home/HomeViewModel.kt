package com.keikuethas.irlhideandseek.mvi.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.MVI_ViewModel
import com.keikuethas.irlhideandseek.mvi.home.HomeResult.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : MVI_HiltViewModel<HomeState, HomeIntent, HomeEffect, HomeResult>(
    initialState = HomeState(),
    savedStateKey = "homeState",
    savedStateHandle = savedStateHandle
) {
    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.CreateGame -> sendEffect(HomeEffect.HostLobby)
            is HomeIntent.EditName -> dispatch(NameEdited(intent.value))
            is HomeIntent.EditRoomName -> dispatch(RoomNameEdited(intent.value))
            is HomeIntent.JoinGame -> joinGame()
            HomeIntent.GrantPermissions -> sendEffect(HomeEffect.OpenSettings)
            HomeIntent.DismissError -> dispatch(ErrorDismissed)
        }
    }


    private fun joinGame() {
        val gameExists = true //SERVER запрос к серверу

        if (gameExists) sendEffect(HomeEffect.JoinLobby(state.value.roomNameText))
        else dispatch(
            Error(
                "Игра не найдена",
                "Не удалось найти игру с таким кодом. " +
                        "Пожалуйста, проверьте корректность ввода и повторите попытку."
            )
        )


    }

    override fun reduce(state: HomeState, result: HomeResult) =
        HomeReducer.reduce(state, result)
}