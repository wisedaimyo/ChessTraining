package com.wisedaimyo.chesstraining.chess.model.move

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.pieces.Pawn
import com.wisedaimyo.chesstraining.chess.model.pieces.Piece
import com.wisedaimyo.chesstraining.chess.model.state.Capture
import com.wisedaimyo.chesstraining.chess.model.state.Consequence
import com.wisedaimyo.chesstraining.chess.model.state.KingSideCastle
import com.wisedaimyo.chesstraining.chess.model.state.PreMove
import com.wisedaimyo.chesstraining.chess.model.state.PrimaryMove
import com.wisedaimyo.chesstraining.chess.model.state.Promotion
import com.wisedaimyo.chesstraining.chess.model.state.QueenSideCastle
import kotlinx.parcelize.Parcelize
import java.util.EnumSet


@Parcelize
data class BoardMove(
    val move: PrimaryMove,
    val preMove: PreMove? = null,
    val consequence: Consequence? = null,
    val ambiguity: EnumSet<Ambiguity> = EnumSet.noneOf(Ambiguity::class.java)
) : Parcelable {

    enum class Ambiguity {
        AMBIGUOUS_FILE, AMBIGUOUS_RANK,
    }

    val from: Position = move.from

    val to: Position = move.to

    val piece: Piece = move.piece

    override fun toString(): String =
        toString(useFigurineNotation = true)

    fun toString(useFigurineNotation: Boolean): String {
        if (move is KingSideCastle) return "O-O"
        if (move is QueenSideCastle) return "O-O-O"
        val isCapture = preMove is Capture
        val symbol = when {
            piece !is Pawn -> if (useFigurineNotation) piece.symbol else piece.textSymbol
            isCapture -> from.fileAsLetter
            else -> ""
        }
        val file = if (ambiguity.contains(Ambiguity.AMBIGUOUS_FILE)) from.fileAsLetter.toString() else ""
        val rank = if (ambiguity.contains(Ambiguity.AMBIGUOUS_RANK)) from.rank.toString() else ""
        val capture = if (isCapture) "x" else ""
        val promotion = if (consequence is Promotion) "=${consequence.piece.textSymbol}" else ""

        return "$symbol$file$rank$capture$to$promotion"
    }
}
