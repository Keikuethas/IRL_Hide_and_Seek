package com.keikuethas.irlhideandseek.websocket.outgoing

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/*
* Исходящие сообщения (клиент -> сервер)
 */
sealed interface OutgoingMessage {

    @Serializable
    @SerialName(value = "ping")
    data class Ping(val data: JsonObject = JsonObject(emptyMap())) : OutgoingMessage

    @Serializable
    @SerialName(value = "update_location")
    data class UpdateLocation(val data: LocationData) : OutgoingMessage

    @Serializable
    @SerialName(value = "use_ability")
    data class UseAbility(
        val data: UseAbilityData
    ) : OutgoingMessage

    @Serializable
    @SerialName(value = "change_role")
    data class ChangeRole(val data: ChangeRoleData) : OutgoingMessage

    @Serializable
    @SerialName(value = "change_ready_status")
    data class ChangeReadyStatus(val data: ChangeReadyStatusData ) : OutgoingMessage

    @Serializable
    @SerialName(value = "get_game_state")
    data class GetGameState(val data: JsonObject = JsonObject(emptyMap())) : OutgoingMessage

    @Serializable
    @SerialName(value = "hunter_found_player")
    data class HunterFoundPlayer( val data: HunterFoundPlayerData) : OutgoingMessage
}