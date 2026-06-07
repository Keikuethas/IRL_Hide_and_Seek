package com.keikuethas.irlhideandseek.websocket.outgoing

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/*
* Исходящие сообщения (клиент -> сервер)
 */
sealed interface OutgoingMessage {

    @Serializable
    data class Ping(
        val type: String = "ping",
        val data: JsonObject = JsonObject(emptyMap())
    ) : OutgoingMessage

    @Serializable
    data class UpdateLocation(
        val type: String = "update_location",
        val data: LocationData
    ) : OutgoingMessage

    @Serializable
    data class UseAbility(
        val type: String = "use_ability",
        val data: UseAbilityData
    ) : OutgoingMessage

    @Serializable
    data class ChangeRole(
        val type: String = "change_role",
        val data: ChangeRoleData
    ) : OutgoingMessage

    @Serializable
    data class ChangeReadyStatus(
        val type: String = "change_ready_status",
        val data: ChangeReadyStatusData
    ) : OutgoingMessage

    @Serializable
    data class GetGameState(
        val type: String = "get_game_state",
        val data: JsonObject = JsonObject(emptyMap())
    ) : OutgoingMessage

    @Serializable
    data class HunterFoundPlayer(
        val type: String = "hunter_found_player",
        val data: HunterFoundPlayerData
    ) : OutgoingMessage
}