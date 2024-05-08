package com.wisedaimyo.chesstraining.main.data.models.model

import com.google.firebase.Timestamp

data class PlayedGame(
    val black: String? = null,
    val blackElo: Int? = null,
    val whiteElo: Int? = null,
    val white: String? = null,
    val fen: String? = null,
    val lastMove: Timestamp? = null,
    val moves: List<String>? = null,
    val time: Int? = null,
    val trainer: List<String>? = null,
    val winner: String? = null,
    val wonBy: String? = null
)