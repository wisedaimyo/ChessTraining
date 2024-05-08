package com.wisedaimyo.chesstraining.chess.model.pieces

enum class SetPiece {
    WHITE, BLACK;

    fun change() =
        when (this) {
            WHITE -> BLACK
            BLACK -> WHITE
        }
}