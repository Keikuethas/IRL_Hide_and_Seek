package com.keikuethas.irlhideandseek.utils

import com.keikuethas.irlhideandseek.FrequencyType
import com.keikuethas.irlhideandseek.Websocket.EventType
import java.security.InvalidParameterException

val EventType.Name: String get() = when(this) {
    EventType.BOMB -> "Бомба"
    EventType.AIRDROP -> "Подарок небес"
    EventType.BOMBARDMENT -> "Бомбардировка"
    EventType.REVEAL -> "Подсветка"
}

val EventType.Description: String get() = when(this) {
    EventType.BOMB -> "На карту падает бомба с обратным отсчётом."
    EventType.AIRDROP -> "На карте появляется зона выдачи сильной способности, которую может забрать только один."
    EventType.BOMBARDMENT -> "Усыпает карту бомбами, которые вот-вот взорвутся."
    EventType.REVEAL -> "На короткое время все игроки становятся видны на карте."
}

val FrequencyType.Name: String get() = when(this) {
    FrequencyType.FREQUENT -> "Часто"
    FrequencyType.RARE -> "Редко"
    FrequencyType.COMMON -> "Обычно"
}

fun FrequencyTypeByName(name: String): FrequencyType {
    FrequencyType.entries.forEach { if (it.Name == name) return it}
    throw InvalidParameterException("Unknown frequency: $name")
}