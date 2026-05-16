package com.keikuethas.irlhideandseek.Websocket

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

// Базовый конверт вебсокета
data class WsEnvelope(
    @SerializedName("type") val type: String,
    @SerializedName("data") val data: JsonElement?
)

// ==========================================
// ИСХОДЯЩИЕ ЗАПРОСЫ (КЛИЕНТ -> СЕРВЕР)
// ==========================================

object OutgoingRequests {
    fun ping() = mapOf<String, Any>()

    fun updateLocation(lat: Double, lng: Double) = mapOf(
        "lat" to lat,
        "lng" to lng
    )

    fun useAbility(abilityType: AbilityType) = mapOf(
        "ability_type" to abilityType.name
    )

    fun getGameState() = mapOf<String, Any>()

    fun changeRole(roleId: String) = mapOf(
        "role_id" to roleId
    )

    fun changeReadyStatus(isReady: Boolean) = mapOf(
        "status" to isReady
    )
}

// ==========================================
// ВХОДЯЩИЕ СОБЫТИЯ (СЕРВЕР -> КЛИЕНТ)
// ==========================================

sealed interface ServerEvent {
    // Системные
    data class Pong(val serverTime: String) : ServerEvent
    data class InitialConnected(val player: PlayerData, val game: GameData) : ServerEvent

    // Игровой процесс
    data class PlayerMoved(
        val playerId: String,
        val lat: Double,
        val lng: Double,
        val timestamp: String
    ) : ServerEvent

    data class AbilityUsed(val ability: AbilityType, val result: Int) : ServerEvent
    data class FullGameState(val game: GameData) : ServerEvent
    data class ZoneCreated(
        val zoneId: String,
        val zoneType: ZoneType,
        val centerLat: Double,
        val centerLng: Double,
        val radius: Double
    ) : ServerEvent

    data class ZoneDeleted(val zoneId: String) : ServerEvent

    // Лобби
    data class RoleChanged(val roleId: String) : ServerEvent
    data class ReadyStatusChanged(val status: Boolean) : ServerEvent

    // Таймер
    object TimerToHideFinished : ServerEvent

    // Если пришел неизвестный пакет
    data class Unknown(val rawType: String) : ServerEvent
}

// DTO для парсинга специфичных тел ответов
data class InitialConnectedPayload(
    @SerializedName("player_data") val player: PlayerData,
    @SerializedName("game_data") val game: GameData
)

data class PongPayload(@SerializedName("server_time") val serverTime: String)
data class PlayerMovedPayload(
    @SerializedName("player_id") val playerId: String,
    @SerializedName("location_lat") val lat: Double,
    @SerializedName("location_lng") val lng: Double,
    @SerializedName("timestamp") val timestamp: String
)

data class AbilityUsedPayload(
    @SerializedName("ability") val ability: AbilityType,
    @SerializedName("result") val result: Int
)

data class RoleChangedPayload(@SerializedName("role_id") val roleId: String)
data class ReadyStatusPayload(@SerializedName("status") val status: Boolean)
data class CreateZonePayload(
    @SerializedName("zone_id") val zoneId: String,
    @SerializedName("zone_type") val zoneType: ZoneType,
    @SerializedName("center_lat") val centerLat: Double,
    @SerializedName("center_lng") val centerLng: Double,
    @SerializedName("radius") val radius: Double
)

data class DeleteZonePayload(@SerializedName("zone_id") val zoneId: String)