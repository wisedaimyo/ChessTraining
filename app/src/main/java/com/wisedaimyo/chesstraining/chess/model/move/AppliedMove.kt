package com.wisedaimyo.chesstraining.chess.model.move

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.pieces.Piece
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece.*
import com.wisedaimyo.chesstraining.chess.model.state.PrimaryMove
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class AppliedMove(
    val boardMove: BoardMove,
    val effect: MoveEffect? = null
) : Parcelable {

    @IgnoredOnParcel
    val move: PrimaryMove = boardMove.move

    @IgnoredOnParcel
    val from: Position = move.from

    @IgnoredOnParcel
    val to: Position = move.to

    @IgnoredOnParcel
    val piece: Piece = move.piece

    override fun toString(): String =
        toString(
            useFigurineNotation = true,
            includeResult = true
        )

    fun toString(useFigurineNotation: Boolean, includeResult: Boolean): String {
        val postFix = when {
            effect == MoveEffect.CHECK -> "+"
            includeResult && effect == MoveEffect.CHECKMATE -> "#  ${if (boardMove.move.piece.setPiece == WHITE) "1-0" else "0-1"}"
            includeResult && effect == MoveEffect.DRAW -> "  ½ - ½"
            else -> ""
        }
        return "${boardMove.toString(useFigurineNotation)}$postFix"
    }
}