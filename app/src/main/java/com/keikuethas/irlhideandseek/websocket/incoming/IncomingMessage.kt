package com.keikuethas.irlhideandseek.websocket.incoming

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/*
* Входящие сообщения (сервер -> клиент)
*/
@Serializable
sealed interface IncomingMessage {
    @Serializable
    @SerialName("pong")
    data class Pong(val data: PongData) : IncomingMessage

    @Serializable
    @SerialName("websocket_connected_player")
    data class WebSocketConnectedPlayer(val data: ConnectedPlayerData) : IncomingMessage

    @Serializable
    @SerialName("player_moved")
    data class PlayerMoved(val data: PlayerMovedData) : IncomingMessage

    @Serializable
    @SerialName("role_changed")
    data class RoleChanged(val data: RoleChangedData) : IncomingMessage

    @Serializable
    @SerialName("player_role_changed")
    data class PlayerRoleChanged(val data: PlayerRoleChangedData): IncomingMessage

    @Serializable
    @SerialName("ready_status_changed")
    data class ReadyStatusChanged(val data: ReadyStatusChangedData) : IncomingMessage

    @Serializable
    @SerialName("player_ready_status_changed")
    data class PlayerReadyStatusChanged(val data: PlayerReadyStatusChangedData) : IncomingMessage

    @Serializable
    @SerialName("game_state")
    data class GameState(val data: GameStateData) : IncomingMessage

    @Serializable
    @SerialName("create_zone")
    data class CreateZone(val data: ZoneData) : IncomingMessage

    @Serializable
    @SerialName("delete_zone")
    data class DeleteZone(val data: DeleteZoneData) : IncomingMessage

    @Serializable
    @SerialName("player_online")
    data class PlayerOnline(val data: PlayerOnlineData) : IncomingMessage

    @Serializable
    @SerialName("player_offline")
    data class PlayerOffline(val data: PlayerOfflineData) : IncomingMessage

    @Serializable
    @SerialName("you_died")
    data class YouDied(val data: YouDiedData) : IncomingMessage

    @Serializable
    @SerialName("player_died")
    data class PlayerDied(val data: PlayerDiedData) : IncomingMessage

    @Serializable
    @SerialName("start_timer_for_game")
    data class StartTimerForGame(val data: TimerData) : IncomingMessage

    @Serializable
    @SerialName("game_finished")
    data class GameFinished(val data: GameFinishedData) : IncomingMessage

    @Serializable
    @SerialName("timer_to_hide_finished")
    data class TimerToHideFinished(val data: JsonObject) : IncomingMessage

    @Serializable
    @SerialName("start_timer_to_hide")
    data class StartTimerToHide(val data: TimerData) : IncomingMessage

    @Serializable
    @SerialName("ability_used")
    data class AbilityUsed(val data: AbilityUsedData) : IncomingMessage

    @Serializable
    @SerialName("error")
    data class Error(
        val message: String
    ) : IncomingMessage

@Serializable
@SerialName("player_entered_zone")
data class PlayerEnteredZone(val data: PlayerEnteredZoneData) : IncomingMessage

@Serializable
@SerialName("player_exited_zone")
data class PlayerExitedZone(val data: PlayerExitedZoneData) : IncomingMessage

@Serializable
@SerialName("airdrop_collected")
data class AirdropCollected(val data: AirdropCollectedData) : IncomingMessage



}