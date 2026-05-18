package com.keikuethas.irlhideandseek.network

import com.keikuethas.irlhideandseek.network.models.CreateGameRequest
import com.keikuethas.irlhideandseek.network.models.CreateGameResponse
import com.keikuethas.irlhideandseek.network.models.JoinGameRequest
import com.keikuethas.irlhideandseek.network.models.JoinGameResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/games/create")
    suspend fun createGame(
        @Body request: CreateGameRequest
    ): CreateGameResponse

    @POST("api/connect_player/{game_code}")
    suspend fun joinGame(
        @Path("game_code") gameCode: String,
        @Body request: JoinGameRequest
    ): JoinGameResponse

    companion object {
        fun joinGame(roomNameText: String, request: JoinGameRequest) {}
    }
}