package com.wisedaimyo.chesstraining.chess.model.pieces

import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import kotlinx.parcelize.Parcelize

@Parcelize
class Star(override val setPiece: SetPiece) : Piece {

    override val value: Int = 3

    override val asset: Int =
        when (setPiece) {
            SetPiece.WHITE -> R.drawable.star
            SetPiece.BLACK -> R.drawable.star
        }

    override val symbol: String = when (setPiece) {
        SetPiece.WHITE -> "⭐️"
        SetPiece.BLACK -> "⭐"
    }

    override val textSymbol: String = "Star"

    override fun pseudoLegalMoves(gameSnapshotState: GameSnapshotState, checkCheck: Boolean): List<BoardMove> =
        lineMoves(gameSnapshotState, directions)

    companion object {
        val directions = listOf(0 to 0)
    }


}