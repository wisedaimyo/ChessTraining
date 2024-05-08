package com.wisedaimyo.chesstraining.chess.model.pieces

import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import kotlinx.parcelize.Parcelize

@Parcelize
class Knight(override val setPiece: SetPiece) : Piece {

    override val value: Int = 3

    override val asset: Int =
        when (setPiece) {
            SetPiece.WHITE -> R.drawable.knight_white
            SetPiece.BLACK -> R.drawable.knight_black
        }

    override val symbol: String = when (setPiece) {
        SetPiece.WHITE -> "♘"
        SetPiece.BLACK -> "♞"
    }

    override val textSymbol: String = "N"

    override fun pseudoLegalMoves(gameSnapshotState: GameSnapshotState, checkCheck: Boolean): List<BoardMove> =
        targets
            .map { singleCaptureMove(gameSnapshotState, it.first, it.second) }
            .filterNotNull()

    companion object {
        val targets = listOf(
            -2 to 1,
            -2 to -1,
            2 to 1,
            2 to -1,
            1 to 2,
            1 to -2,
            -1 to 2,
            -1 to -2
        )
    }
}