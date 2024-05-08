package com.wisedaimyo.chesstraining.chess.model.pieces

import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.ChessFile
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.model.state.KingSideCastle
import com.wisedaimyo.chesstraining.chess.model.state.Move
import com.wisedaimyo.chesstraining.chess.model.state.QueenSideCastle
import kotlinx.parcelize.Parcelize

@Parcelize
class King(override val setPiece: SetPiece) : Piece {

    override val value: Int = Int.MAX_VALUE

    override val asset: Int =
        when (setPiece) {
            SetPiece.WHITE -> R.drawable.king_white
            SetPiece.BLACK -> R.drawable.king_black
        }

    override val symbol: String = when (setPiece) {
        SetPiece.WHITE -> "♔"
        SetPiece.BLACK -> "♚"
    }

    override val textSymbol: String = SYMBOL

    override fun pseudoLegalMoves(gameSnapshotState: GameSnapshotState, checkCheck: Boolean): List<BoardMove> {
        val moves = targets
            .mapNotNull { singleCaptureMove(gameSnapshotState, it.first, it.second) }
            .toMutableList()

        if (!checkCheck) {
            castleKingSide(gameSnapshotState)?.let { moves += it }
            castleQueenSide(gameSnapshotState)?.let { moves += it }
        }

        return moves
    }

    private fun castleKingSide(
        gameSnapshotState: GameSnapshotState
    ): BoardMove? {
        if (gameSnapshotState.hasCheck()) return null
        if (!gameSnapshotState.castlingInfo[setPiece].canCastleKingSide) return null

        val rank = if (setPiece == SetPiece.WHITE) 1 else 8
        val eSquare = gameSnapshotState.board[ChessFile.e, rank]!!
        val fSquare = gameSnapshotState.board[ChessFile.f, rank]!!
        val gSquare = gameSnapshotState.board[ChessFile.g, rank]!!
        val hSquare = gameSnapshotState.board[ChessFile.h, rank]!!
        if (fSquare.isNotEmpty || gSquare.isNotEmpty) return null
        if (gameSnapshotState.hasCheckFor(fSquare.position) || gameSnapshotState.hasCheckFor(gSquare.position)) return null
        if (hSquare.piece !is Rook) return null

        return BoardMove(
            move = KingSideCastle(this, eSquare.position, gSquare.position),
            consequence = Move(hSquare.piece, hSquare.position, fSquare.position)
        )
    }

    private fun castleQueenSide(
        gameSnapshotState: GameSnapshotState
    ): BoardMove? {
        if (gameSnapshotState.hasCheck()) return null
        if (!gameSnapshotState.castlingInfo[setPiece].canCastleQueenSide) return null

        val rank = if (setPiece == SetPiece.WHITE) 1 else 8
        val eSquare = gameSnapshotState.board[ChessFile.e, rank]!!
        val dSquare = gameSnapshotState.board[ChessFile.d, rank]!!
        val cSquare = gameSnapshotState.board[ChessFile.c, rank]!!
        val bSquare = gameSnapshotState.board[ChessFile.b, rank]!!
        val aSquare = gameSnapshotState.board[ChessFile.a, rank]!!
        if (dSquare.isNotEmpty || cSquare.isNotEmpty || bSquare.isNotEmpty) return null
        if (gameSnapshotState.hasCheckFor(dSquare.position) || gameSnapshotState.hasCheckFor(cSquare.position)) return null
        if (aSquare.piece !is Rook) return null

        return BoardMove(
            move = QueenSideCastle(this, eSquare.position, cSquare.position),
            consequence = Move(aSquare.piece, aSquare.position, dSquare.position)
        )
    }

    companion object {
        const val SYMBOL = "K"
        val targets = listOf(
            -1 to -1,
            -1 to 0,
            -1 to 1,
            0 to 1,
            0 to -1,
            1 to -1,
            1 to 0,
            1 to 1,
        )
    }
}