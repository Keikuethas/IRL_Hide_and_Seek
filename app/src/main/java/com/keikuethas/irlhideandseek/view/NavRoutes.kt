package com.keikuethas.irlhideandseek.view

import com.keikuethas.irlhideandseek.PlayerRole
import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data object Lobby

@Serializable
data class Game(val role: PlayerRole)

@Serializable data object RolesSettings
@Serializable data object RulesSettings
@Serializable data object MapSettings
@Serializable data object AbilitiesSettings
@Serializable data object LobbySettings
/*
* Описание экранов:
* Home -> ввод имени и идентификатора комнаты, либо создание своей комнаты
* Lobby -> настройка комнаты: параметры бонусов, роли игроков
* Game -> собственно игра. Карта, активация бонусов, прятки
* */