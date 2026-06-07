package com.keikuethas.irlhideandseek.data

import android.util.Log
import com.keikuethas.irlhideandseek.LocationEvent
import com.keikuethas.irlhideandseek.LocationProvider
import com.keikuethas.irlhideandseek.data.GameEvent.CreateZone
import com.keikuethas.irlhideandseek.data.GameEvent.DeleteZone
import com.keikuethas.irlhideandseek.data.GameEvent.GameFinished
import com.keikuethas.irlhideandseek.data.GameEvent.PlayerDied
import com.keikuethas.irlhideandseek.data.GameEvent.PlayerQuit
import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.websocket.WebSocketManager
import com.keikuethas.irlhideandseek.websocket.incoming.IncomingMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val webSocketManager: WebSocketManager
) {

    private val _gameEvents = MutableSharedFlow<GameEvent>(
        replay = 1,
        extraBufferCapacity = 64
    )
    val gameEvents: SharedFlow<GameEvent> = _gameEvents.asSharedFlow()

    // Мы заранее знаем, что подключились к игре через лобби
    fun connect(scope: CoroutineScope) {
        scope.launch { observeMessages() }
        scope.launch { observeLocation() }
    }

    // NOTE: вот это я сделал сам, и оно может не работать
    suspend fun observeLocation() =
        LocationProvider.observeLocation().collect { event ->
            when(event) {
                LocationEvent.PermissionRevoked ->
                    _gameEvents.emit(GameEvent.LocationPermissionRevoked)
                LocationEvent.ProvidersDisabled ->
                    _gameEvents.emit(GameEvent.LocationProvidersDisabled)
                is LocationEvent.Update ->
                    _gameEvents.emit(
                        GameEvent.LocationUpdated(
                            latitude = event.location.latitude,
                            longitude = event.location.longitude
                        )
                    )
            }
        }

    // Инициализация подписки на сообщения
    suspend fun observeMessages(): Nothing =
        webSocketManager.incomingMessages.collect { message ->
            with(message) {
                Log.d("GameRepo", "Raw message: $this")

                when (this) {

                    is IncomingMessage.PlayerOffline ->
                        _gameEvents.emit(PlayerQuit(data.player_id))


                    is IncomingMessage.Error ->
                        _gameEvents.emit(GameEvent.Error(toString()))


                    is IncomingMessage.CreateZone ->
                        _gameEvents.emit(
                            value = CreateZone(
                                data.zone_id,
                                data.zone_type,
                                data.center_lat,
                                data.center_lng,
                                data.radius
                            )
                        )

                    is IncomingMessage.DeleteZone ->
                        _gameEvents.emit(
                            value = DeleteZone(zoneId = data.zone_id)
                        )

                    is IncomingMessage.GameFinished ->
                        _gameEvents.emit(
                            value = GameFinished(data.is_victory)
                        )

                    is IncomingMessage.GameState ->
                        _gameEvents.emit(
                            GameEvent.GameState(
                                game = data.game_info,
                                player = data.player_info
                            )
                        )

                    is IncomingMessage.PlayerDied ->
                        _gameEvents.emit(
                            value = PlayerDied(
                                playerId = data.player_id,
                                hunterId = data.hunter_player_id,
                                reason = data.reason
                            )
                        )

                    is IncomingMessage.PlayerMoved ->
                        _gameEvents.emit(
                            GameEvent.PlayerMoved(
                                playerId = data.player_id,
                                locationLat = data.location_lat,
                                locationLng = data.location_lng,
                                timestamp = data.timestamp
                            )
                        )

                    is IncomingMessage.StartTimerForGame ->
                        _gameEvents.emit(
                            GameEvent.StartTimerForGame(
                                duration = data.duration_seconds
                            )
                        )

                    is IncomingMessage.YouDied ->
                        _gameEvents.emit(
                            GameEvent.YouDied(
                                data.hunter_player_id,
                                reason = data.reason
                            )
                        )

                    is IncomingMessage.AbilityUsed ->
                        _gameEvents.emit(
                            GameEvent.AbilityUsed(
                                ability = data.ability,
                                result = data.result
                            )
                        )

                    is IncomingMessage.PlayerEnteredZone ->
                        _gameEvents.emit(
                            GameEvent.PlayerEnteredZone(
                                zoneId = data.zone_id,
                                zoneType = data.zone_type,
                                centerLat = data.center_lat,
                                centerLng = data.center_lng,
                                radius = data.radius
                            )
                        )

                    is IncomingMessage.PlayerExitedZone ->
                        _gameEvents.emit(
                            GameEvent.PlayerExitedZone(
                                zoneId = data.zone_id,
                                zoneType = data.zone_type
                            )
                        )

                    is IncomingMessage.AirdropCollected ->
                        _gameEvents.emit(
                            GameEvent.AirdropCollected(
                                abilityId = data.ability.id,
                                ability = data.ability.ability_type,
                                numberUses = data.ability.number_uses,
                                rechargeTime = data.ability.recharge_time,
                                durationSeconds = data.ability.duration_seconds,
                                data = data.ability.data,
                            )
                        )

                    else -> {/*игнорируем неигровые события*/
                    }
                }
            }
        }

    fun disconnect() =
        webSocketManager.disconnect()

    suspend fun catchPlayer(playerId: String) =
        webSocketManager.sendHunterFoundPlayer(playerId)

    suspend fun useAbility(
        type: AbilityType,
        centerLat: Double? = null,
        centerLng: Double? = null
    ) =
        webSocketManager.sendUseAbility(
            abilityType = type,
            centerLat = centerLat,
            centerLng = centerLng
        )

    suspend fun updateLocation(
        latitude: Double,
        longitude: Double
    ) =
        webSocketManager.sendLocation(
            lat = latitude,
            lng = longitude
        )

}