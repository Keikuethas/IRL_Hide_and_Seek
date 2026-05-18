package com.keikuethas.irlhideandseek.view

import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data class Lobby(
    val playerName: String,
    val roomName: String,
    val gameId: String,
    val playerId: String
)

@Serializable
data object Game

// --- экраны при создании игры ---
@Serializable data object NewGame
@Serializable data object RolesSettings
@Serializable data object MapSettings
@Serializable data object LobbySettings
/*
* Описание экранов:
* Home -> ввод имени и идентификатора комнаты, либо создание своей комнаты
* Lobby -> настройка комнаты: параметры бонусов, роли игроков
* Game -> собственно игра. Карта, активация бонусов, прятки
* */