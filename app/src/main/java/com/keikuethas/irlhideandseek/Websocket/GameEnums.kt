package com.keikuethas.irlhideandseek.Websocket

import com.google.gson.annotations.SerializedName

enum class VictoryCondition {
    @SerializedName("HIDER") HIDER,
    @SerializedName("SEEKER") SEEKER
}

enum class GameStatus {
    @SerializedName("WAITING") WAITING,
    @SerializedName("HIDE_TIME") HIDE_TIME,
    @SerializedName("ACTIVE") ACTIVE,
    @SerializedName("FINISHED") FINISHED
}

enum class AbilityType {
    @SerializedName("SHIELD") SHIELD,
    @SerializedName("INTEL") INTEL,
    @SerializedName("SCAN") SCAN,
    @SerializedName("PERSONAL_BOMB") PERSONAL_BOMB,
    @SerializedName("SNARE") SNARE,
    @SerializedName("TRAP") TRAP,
    @SerializedName("SAFE_HOUSE") SAFE_HOUSE,
    @SerializedName("SAFE_MANSION") SAFE_MANSION
}

enum class EventType {
    @SerializedName("BOMB") BOMB,
    @SerializedName("AIRDROP") AIRDROP,
    @SerializedName("BOMBARDMENT") BOMBARDMENT,
    @SerializedName("REVEAL") REVEAL
}

enum class ActivationFrequency {
    @SerializedName("FREQUENT") FREQUENT,
    @SerializedName("COMMON") COMMON,
    @SerializedName("RARE") RARE
}

enum class ZoneType {
    @SerializedName("SAFE") SAFE,
    @SerializedName("DANGER") DANGER,
    @SerializedName("WARNING") WARNING,
    @SerializedName("AIRDROP") AIRDROP,
    @SerializedName("SNARE") SNARE,
    @SerializedName("TRAP") TRAP,
    @SerializedName("SAFE_HOUSE") SAFE_HOUSE,
    @SerializedName("SAFE_MANSION") SAFE_MANSION
}