package com.wisedaimyo.chesstraining.main.data.models.model

data class ChessCourse(
    val name: String? = null,
    val type: String? = null,
    val img: String? = null,
    val description: String? = null,
    val fen: String? = null,
    val moves: List<String>? = null,
    val describeMove: List<String>? = null,
)
