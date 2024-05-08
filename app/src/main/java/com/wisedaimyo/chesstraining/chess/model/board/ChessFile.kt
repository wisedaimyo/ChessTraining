package com.wisedaimyo.chesstraining.chess.model.board

enum class ChessFile {
    a, b, c, d, e, f, g, h
}

operator fun ChessFile.get(rank: Int): Position =
    Position.entries[this.ordinal * 8 + (rank - 1)]

operator fun ChessFile.get(rank: Rank): Position =
    Position.entries[this.ordinal * 8 + rank.ordinal]