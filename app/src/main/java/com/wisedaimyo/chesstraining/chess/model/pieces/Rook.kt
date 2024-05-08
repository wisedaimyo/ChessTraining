package com.wisedaimyo.chesstraining.chess.model.pieces

import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import kotlinx.parcelize.Parcelize

@Parcelize
class Rook(override val setPiece: SetPiece) : Piece {

    override val value: Int = 5

    override val asset: Int =
        when (setPiece) {
            SetPiece.WHITE -> R.drawable.rook_white
            SetPiece.BLACK -> R.drawable.rook_black
        }

    override val symbol: String = when (setPiece) {
        SetPiece.WHITE -> "♖"
        SetPiece.BLACK -> "♜"
    }

    override val textSymbol: String = "R"

    override fun pseudoLegalMoves(gameSnapshotState: GameSnapshotState, checkCheck: Boolean): List<BoardMove> =
        lineMoves(gameSnapshotState, directions)

    companion object {
        val directions = listOf(
            0 to -1,
            0 to 1,
            -1 to 0,
            1 to 0,
        )
    }
}