package com.keikuethas.irlhideandseek

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.buildJsonObject

enum class FrequencyType {FREQUENT, RARE, COMMON}

@Serializable
abstract class Event(
    open var activation_frequency: FrequencyType,
    @Transient val additionData: Map<String, Number> = emptyMap()
) {
    val addition_data get() = buildJsonObject {

    }
}

class Bomb(
    override var activation_frequency: FrequencyType,
    duration_seconds: Int = 600,
    radius: Float = 10.0F,
    damage: Int = 100
): Event(
    activation_frequency,
    additionData = mapOf(
        "duration_seconds" to duration_seconds,
        "radius" to radius,
        "damage" to damage
    )
)

class Airdrop(
    override var activation_frequency: FrequencyType,
    radius: Float = 10.0F,
): Event(
    activation_frequency,
    additionData = mapOf(
        "radius" to radius,
    )
)

class Bombardment(
    override var activation_frequency: FrequencyType,
    duration_seconds: Int = 600,
    radius: Float = 5.0F,
    damage: Int = 50
): Event(
    activation_frequency,
    additionData = mapOf(
        "duration_seconds" to duration_seconds,
        "radius" to radius,
        "damage" to damage
    )
)