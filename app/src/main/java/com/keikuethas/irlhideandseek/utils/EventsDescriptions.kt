package com.keikuethas.irlhideandseek.utils

import com.keikuethas.irlhideandseek.model.ActivationFrequency
import com.keikuethas.irlhideandseek.model.EventType

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

val ActivationFrequency.Name: String get() = when(this) {
    ActivationFrequency.FREQUENT -> "Часто"
    ActivationFrequency.RARE -> "Редко"
    ActivationFrequency.COMMON -> "Обычно"
}
