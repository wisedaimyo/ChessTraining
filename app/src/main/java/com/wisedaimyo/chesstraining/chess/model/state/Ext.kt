package com.wisedaimyo.chesstraining.chess.model.state

import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.board.isDarkSquare
import com.wisedaimyo.chesstraining.chess.model.board.isLightSquare
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.pieces.*
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import java.util.EnumSet


fun List<GameSnapshotState>.hasThreefoldRepetition(): Boolean =
    map { it.toRepetitionRelevantState().hashCode() }
        .groupBy { it }
        .map { it.value.size }
        .any { it > 2 }

fun Map<Position, Piece>.hasInsufficientMaterial() =
    when {
        size == 2 && hasWhiteKing() && hasBlackKing() -> true
        size == 3 && hasWhiteKing() && hasBlackKing() && hasBishop() -> true
        size == 3 && hasWhiteKing() && hasBlackKing() && hasKnight() -> true
        size == 4 && hasWhiteKing() && hasBlackKing() && hasBishopsOnSameColor() -> true
        else -> false
    }

private fun Map<Position, Piece>.hasWhiteKing(): Boolean =
    values.find { it.setPiece == SetPiece.WHITE && it is King } != null

private fun Map<Position, Piece>.hasBlackKing(): Boolean =
    values.find { it.setPiece == SetPiece.BLACK && it is King } != null

private fun Map<Position, Piece>.hasBishop(): Boolean =
    values.find { it is Bishop } != null

private fun Map<Position, Piece>.hasKnight(): Boolean =
    values.find { it is Knight } != null

private fun Map<Position, Piece>.hasBishopsOnSameColor(): Boolean {
    val bishops = filter { it.value is Bishop }

    return bishops.size > 1 && (bishops.all { it.key.isLightSquare() } || bishops.all { it.key.isDarkSquare() })
}

fun BoardMove.applyAmbiguity(gameSnapshotState: GameSnapshotState): BoardMove =
    gameSnapshotState.board
        .pieces(gameSnapshotState.toMove)
        .filter { (_, piece) -> piece.textSymbol == this.piece.textSymbol }
        .flatMap { (_, piece) -> piece.pseudoLegalMoves(gameSnapshotState, false) }
        .filter { it.to == this.to }
        .map { it.from }
        .distinct() // promotion moves have same `from`, but are different per target piece, we don't need all of those
        .let { similarPiecePositions ->
            val ambiguity = EnumSet.noneOf(BoardMove.Ambiguity::class.java)
            when (similarPiecePositions.size) {
                1 -> this
                else -> {
                    val onSameFile = similarPiecePositions.filter { it.file == from.file }
                    if (onSameFile.size == 1) {
                        ambiguity.add(BoardMove.Ambiguity.AMBIGUOUS_FILE)
                    } else {
                        val onSameRank = similarPiecePositions.filter { it.rank == from.rank }
                        if (onSameRank.size == 1) {
                            ambiguity.add(BoardMove.Ambiguity.AMBIGUOUS_RANK)
                        } else {
                            ambiguity.add(BoardMove.Ambiguity.AMBIGUOUS_FILE)
                            ambiguity.add(BoardMove.Ambiguity.AMBIGUOUS_RANK)
                        }
                    }

                    copy(ambiguity = ambiguity)
                }
            }
        }
