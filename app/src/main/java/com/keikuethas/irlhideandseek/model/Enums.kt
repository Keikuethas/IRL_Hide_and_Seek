package com.keikuethas.irlhideandseek.model

import kotlinx.serialization.SerialName

enum class DeathReason {
    @SerialName("HUNTER_FOUND_PLAYER") HUNTER_FOUND_PLAYER,
    @SerialName("HP_ARE_OVER") HP_ARE_OVER,
}

enum class ZoneType {
    @SerialName("SAFE") SAFE,
    @SerialName("DANGER") DANGER,
    @SerialName("WARNING") WARNING,
    @SerialName("AIRDROP") AIRDROP,
    @SerialName("SNARE") SNARE,
    @SerialName("TRAP") TRAP,
    @SerialName("SAFE_HOUSE") SAFE_HOUSE,
    @SerialName("SAFE_MANSION") SAFE_MANSION,
}

enum class AbilityType {
    @SerialName("SHIELD") SHIELD,
    @SerialName("INTEL") INTEL,
    @SerialName("SCAN") SCAN,
    @SerialName("SNARE") SNARE,
    @SerialName("PERSONAL_BOMB") PERSONAL_BOMB,
    @SerialName("TRAP") TRAP,
    @SerialName("SAFE_HOUSE") SAFE_HOUSE,
    @SerialName("SAFE_MANSION") SAFE_MANSION,
}

enum class ActivationFrequency {
    @SerialName("FREQUENT") FREQUENT,
    @SerialName("COMMON") COMMON,
    @SerialName("RARE") RARE,
}

enum class GameStatus {

}

enum class EventType {
    @SerialName("REVEAL") REVEAL,
    @SerialName("BOMB") BOMB,
    @SerialName("AIRDROP") AIRDROP,
    @SerialName("BOMBARDMENT") BOMBARDMENT
}