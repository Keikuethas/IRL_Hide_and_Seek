package com.keikuethas.irlhideandseek
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
@Parcelize
data class RoleParams(
    val health: Int = 100,
    val victory_condition: RoleType
): Parcelable

//DESCRIBE
/**
 * Настройки игры. Отправляются на сервер при создании комнаты
 */
@Serializable
data class GameSettings(
    val name: String = "New game",
    val center_lat: Double = 0.0,
    val center_lng: Double = 0.0,
    val safe_zone_radius: Double = 500.0,
    val min_zone_radius: Double = 500.0,
    val zone_shrink_interval: Int = 120,
    val game_duration: Int = 1800,
    val time_to_hide: Int = 300,

    val host_name: String = "host name",
    val host_player_location_lat: Double = 0.0,
    val host_player_location_lng: Double = 0.0,

    val game_roles: Map<String, RoleParams> = emptyMap(),

    val roles_abilities: Map<String, Map<String, Ability>> = emptyMap(),

    val events_configurations: Map<String, Event> = emptyMap(),
    val roles_events: Map<String, List<String>> = emptyMap()
    ) {

    val host_player get() = buildJsonObject {
        put("host_name", host_name)
        put("host_player_location_lat", host_player_location_lat)
        put("host_player_location_lng", host_player_location_lng)
    }

    val events get() = events_configurations.keys.toList()

}