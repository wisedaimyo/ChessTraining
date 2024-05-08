package com.wisedaimyo.chesstraining.main.data.models.model

import com.google.firebase.Timestamp

data class PlayingGame(
    val black: String? = null,
    val white: String? = null,
    val fen: String? = null,
    val lastMove: Timestamp? = null,
    val moves: List<String>? = null,
    val time: Int? = null
)