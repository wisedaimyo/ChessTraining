package com.wisedaimyo.chesstraining.chess.model.state

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.board.Board
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece.*
import com.wisedaimyo.chesstraining.chess.model.board.Position.*
import com.wisedaimyo.chesstraining.chess.model.pieces.*
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import kotlinx.parcelize.Parcelize


@Parcelize
data class CastlingInfo(
    val holders: Map<SetPiece, Holder> = mapOf(
        WHITE to Holder(),
        BLACK to Holder()
    )
) : Parcelable {

    @Parcelize
    data class Holder(
        val kingHasMoved: Boolean = false,
        val kingSideRookHasMoved: Boolean = false,
        val queenSideRookHasMoved: Boolean = false,
    ) : Parcelable {

        val canCastleKingSide: Boolean
            get() = !kingHasMoved && !kingSideRookHasMoved

        val canCastleQueenSide: Boolean
            get() = !kingHasMoved && !queenSideRookHasMoved
    }

    operator fun get(set: SetPiece) = holders[set]!!

    fun apply(boardMove: BoardMove): CastlingInfo {
        val move = boardMove.move
        val piece = boardMove.piece
        val set = piece.setPiece
        val holder = holders[set]!!

        val kingSideRookInitialPosition = if (set == SetPiece.WHITE) h1 else h8
        val queenSideRookInitialPosition = if (set == SetPiece.WHITE) a1 else a8

        val updatedHolder = holder.copy(
            kingHasMoved = holder.kingHasMoved || piece is King,
            kingSideRookHasMoved = holder.kingSideRookHasMoved || piece is Rook && move.from == kingSideRookInitialPosition,
            queenSideRookHasMoved = holder.queenSideRookHasMoved || piece is Rook && move.from == queenSideRookInitialPosition,
        )

        return copy(
            holders = holders
                .minus(set)
                .plus(set to updatedHolder)
        )
    }

    companion object {
        fun from(board: Board): CastlingInfo {
            val whitePieces = board.pieces(SetPiece.WHITE)
            val whiteHolder = Holder(
                kingHasMoved = whitePieces[e1] !is King,
                kingSideRookHasMoved = whitePieces[h1] !is Rook,
                queenSideRookHasMoved = whitePieces[a1] !is Rook,
            )
            val blackPieces = board.pieces(BLACK)
            val blackHolder = Holder(
                kingHasMoved = blackPieces[e8] !is King,
                kingSideRookHasMoved = blackPieces[h8] !is Rook,
                queenSideRookHasMoved = blackPieces[a8] !is Rook,
            )

            return CastlingInfo(
                mapOf(
                    WHITE to whiteHolder,
                    BLACK to blackHolder
                )
            )
        }
    }
}