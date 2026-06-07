package com.keikuethas.irlhideandseek.model

import kotlinx.serialization.SerialName

enum class VictoryCondition {
    @SerialName("SEEKER") Seeker,
    @SerialName("HIDER") Hider,
}

enum class DeathReason {
    @SerialName("HUNTER_FOUND_PLAYER") HunterFoundPlayer,
    @SerialName("HP_ARE_OVER") HpAreOver,
}

enum class ZoneType {
    @SerialName("SAFE") Safe,
    @SerialName("Danger") Danger,
    @SerialName("Warning") Warning,
    @SerialName("AIRDROP") Airdrop,
    @SerialName("SNARE") Snare,
    @SerialName("TRAP") Trap,
    @SerialName("SAFE_HOUSE") SafeHouse,
    @SerialName("SAFE_MANSION") SafeMansion,
}

enum class AbilityType {
    @SerialName("SHIELD") Shield,
    @SerialName("INTEL") Intel,
    @SerialName("SCAN") Scan,
    @SerialName("SNARE") Snare,
    @SerialName("PERSONAL_BOMB") PersonalBomb,
    @SerialName("TRAP") Trap,
    @SerialName("SAFE_HOUSE") SafeHouse,
    @SerialName("SAFE_MANSION") SafeMansion,
}

enum class ActivationFrequency {
    @SerialName("FREQUENT") Frequent,
    @SerialName("COMMON") Common,
    @SerialName("RARE") Rare,
}

enum class GameStatus {

}