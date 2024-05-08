package com.wisedaimyo.chesstraining.main.data.models.model

data class TrainerChessTask(
    val name: String? = null,
    val type: String? = null,
    val createdBy: String? = null,
    val image: String? = null,
    val description: String? = null,
    val fen: String? = null,
    val moves: List<String>? = null,
    val done: List<String>? = null,
    val describeMove: List<String>? = null,
)
