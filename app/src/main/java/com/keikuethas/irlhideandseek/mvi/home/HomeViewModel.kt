package com.keikuethas.irlhideandseek.mvi.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.keikuethas.irlhideandseek.LocationProvider
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.home.HomeResult.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.keikuethas.irlhideandseek.network.models.JoinGameRequest
import com.keikuethas.irlhideandseek.network.ApiService
import com.keikuethas.irlhideandseek.mvi.home.HomeResult.Error
import com.keikuethas.irlhideandseek.network.models.CreateGameRequest
import com.keikuethas.irlhideandseek.network.models.HostPlayer
import retrofit2.HttpException

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiService: ApiService
) : MVI_HiltViewModel<HomeState, HomeIntent, HomeEffect, HomeResult>(
    initialState = HomeState(isLoading = false),
    savedStateKey = "homeState",
    savedStateHandle = savedStateHandle
) {
    init {
        // ✅ Проверяем разрешения при старте
        dispatch(if (LocationProvider.hasLocationPermission) PermissionGranted else PermissionDenied)
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.CreateGame -> createGame()
            is HomeIntent.EditName -> dispatch(HomeResult.NameEdited(intent.value))
            is HomeIntent.EditRoomName -> {
                dispatch(RoomNameEdited(intent.value))
                if (intent.value.length == 6) joinGame()
            }
            HomeIntent.GrantPermissions -> sendEffect(HomeEffect.OpenSettings)
            HomeIntent.DismissError -> dispatch(HomeResult.ErrorDismissed)

            // ✅ Обработка результата системного диалога
            is HomeIntent.PermissionResult -> {
                dispatch(if (intent.granted) PermissionGranted else PermissionDenied)
            }

            HomeIntent.RequestLocationPermission -> {
                // ✅ Триггер для Compose-слоя (запускается через ActivityResultLauncher)
                // В чистой MVI ViewModel не знает об Android UI, поэтому эффект не шлём,
                // а Compose сам перехватит этот Intent и вызовет launcher.
            }
        }
    }

    private fun createGame() {
        if (state.value.nameText.isBlank() || state.value.roomNameText.isBlank()) {
            dispatch(Error("Ошибка", "Введите имя и название комнаты"))
            return
        }
        viewModelScope.launch {
            dispatch(HomeResult.Loading(true))
            val request = CreateGameRequest(
                name = state.value.roomNameText,
                center_lat = 55.751244,  // TODO: реальные координаты
                center_lng = 37.618423,
                safe_zone_radius = 500.0,
                min_zone_radius = 50.0,
                zone_shrink_interval = 120,
                game_duration = 1800,
                time_to_hide = 300,
                host_player = HostPlayer(
                    host_name = state.value.nameText,
                    host_player_location_lat = 55.751244,
                    host_player_location_lng = 37.618423
                ),
                game_roles = emptyMap(),
                roles_abilities = emptyMap(),
                events = emptyList(),
                roles_events = emptyMap(),
                events_configurations = emptyMap()
            )
            try {
                val response = apiService.createGame(request)
                dispatch(HomeResult.Loading(false))
                sendEffect(
                    HomeEffect.JoinLobby(
                        playerName = state.value.nameText,
                        roomName = state.value.roomNameText,
                        gameId = response.game.id,
                        playerId = response.host_player_id
                    )
                )
            } catch (e: HttpException) {
                dispatch(HomeResult.Loading(false))
                when (e.code()) {
                    400 -> dispatch(Error("Некорректный запрос", "Проверьте данные игры"))
                    409 -> dispatch(Error("Имя занято", "Игра с таким названием уже существует"))
                    else -> dispatch(Error("Ошибка", e.message() ?: "Неизвестная ошибка"))
                }
            } catch (e: Exception) {
                dispatch(HomeResult.Loading(false))
                dispatch(Error("Ошибка подключения", e.message ?: "Проверьте интернет-соединение"))
            }
        }
    }

    private fun joinGame() {
        if (state.value.roomNameText.isBlank()) {
            dispatch(Error("Ошибка", "Введите код комнаты"))
            return
        }
        viewModelScope.launch {
            dispatch(HomeResult.Loading(true))
            val request = JoinGameRequest(
                name = state.value.nameText,
                player_location_lat = 20.9, //getCurrentLocationLat()
                player_location_lng = 30.1 //getCurrentLocationLng()
            )
            Log.i("websocket", request.toString())
            Log.i("websocket", state.value.roomNameText)
            try {
                val response = apiService.joinGame(state.value.roomNameText, request)
                print(response)
                dispatch(HomeResult.Loading(false))
                sendEffect(
                    HomeEffect.JoinLobby(
                        playerName = state.value.nameText,
                        roomName = state.value.roomNameText,
                        gameId = response.game_id,
                        playerId = response.player_id
                    )
                )
            } catch (e: HttpException) {
                dispatch(HomeResult.Loading(false))
                when (e.code()) {
                    404 -> dispatch(Error("Игра не найдена", "Не удалось найти игру с таким кодом"))
                    400 -> dispatch(Error("Некорректный запрос", "Проверьте введённые данные"))
                    409 -> dispatch(Error("Имя уже занято", "Игрок с таким именем уже в игре"))
                    else -> dispatch(Error("Ошибка", e.message() ?: "Неизвестная ошибка"))
                }
            } catch (e: Exception) {
                dispatch(HomeResult.Loading(false))
                dispatch(Error("Ошибка подключения", e.message ?: "Проверьте интернет-соединение"))
            }
        }
    }


//    private fun joinGame() {
//        val gameExists = true //SERVER запрос к серверу
//
//        if (gameExists) sendEffect(HomeEffect.JoinLobby(state.value.roomNameText))
//        else dispatch(
//            Error(
//                "Игра не найдена",
//                "Не удалось найти игру с таким кодом. " +
//                        "Пожалуйста, проверьте корректность ввода и повторите попытку."
//            )
//        )
//
//
//    }

    override fun reduce(state: HomeState, result: HomeResult) =
        HomeReducer.reduce(state, result)
}