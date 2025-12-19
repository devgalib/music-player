package com.example.myapplication.navigation

import com.example.myapplication.data.Audio
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object AudiosList : Screen()

    @Serializable
    data class PlayerScreen(
        val audios:  List<@Contextual Audio>,
        val position: Int
    ) : Screen()
}