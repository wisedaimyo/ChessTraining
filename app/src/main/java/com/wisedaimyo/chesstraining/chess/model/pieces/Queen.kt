package com.wisedaimyo.chesstraining.chess.model.pieces

import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import kotlinx.parcelize.Parcelize

@Parcelize
class Queen(override val setPiece: SetPiece) : Piece {

    override val value: Int = 9

    override val asset: Int =
        when (setPiece) {
            SetPiece.WHITE -> R.drawable.queen_white
            SetPiece.BLACK -> R.drawable.queen_black
        }

    override val symbol: String = when (setPiece) {
        SetPiece.WHITE -> "♕"
        SetPiece.BLACK -> "♛"
    }

    override val textSymbol: String = "Q"

    override fun pseudoLegalMoves(gameSnapshotState: GameSnapshotState, checkCheck: Boolean): List<BoardMove> =
        lineMoves(gameSnapshotState, Rook.directions + Bishop.directions)
}