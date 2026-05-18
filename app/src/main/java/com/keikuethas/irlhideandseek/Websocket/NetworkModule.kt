package com.keikuethas.irlhideandseek.Websocket

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGameWebSocketClient(): GameWebsocketClient = GameWebsocketClient()
}