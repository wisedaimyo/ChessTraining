package com.wisedaimyo.chesstraining.chess.model.pieces

import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import kotlinx.parcelize.Parcelize
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece.*
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState

@Parcelize
class Bishop(override val setPiece: SetPiece) : Piece {

    override val value: Int = 3

    override val asset: Int =
        when (setPiece) {
            WHITE -> R.drawable.bishop_white
            BLACK -> R.drawable.bishop_black
        }

    override val symbol: String = when (setPiece) {
        WHITE -> "♗"
        BLACK -> "♝"
    }

    override val textSymbol: String = "B"

    override fun pseudoLegalMoves(gameSnapshotState: GameSnapshotState, checkCheck: Boolean): List<BoardMove> =
        lineMoves(gameSnapshotState, directions)

    companion object {
        val directions = listOf(
            -1 to -1,
            -1 to 1,
            1 to -1,
            1 to 1,
        )
    }

}