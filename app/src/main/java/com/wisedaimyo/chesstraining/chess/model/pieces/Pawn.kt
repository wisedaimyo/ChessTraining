package com.wisedaimyo.chesstraining.chess.model.pieces

import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Board
import com.wisedaimyo.chesstraining.chess.model.board.Square
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.state.Capture
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.model.state.Move
import com.wisedaimyo.chesstraining.chess.model.state.Promotion
import kotlinx.parcelize.Parcelize

@Parcelize
class Pawn(override val setPiece: SetPiece) : Piece {

    override val value: Int = 1

    override val asset: Int =
        when (setPiece) {
            SetPiece.WHITE -> R.drawable.pawn_white
            SetPiece.BLACK -> R.drawable.pawn_black
        }

    override val symbol: String = when (setPiece) {
        SetPiece.WHITE -> "♙"
        SetPiece.BLACK -> "♟︎"
    }

    override val textSymbol: String = ""

    override fun pseudoLegalMoves(gameSnapshotState: GameSnapshotState, checkCheck: Boolean): List<BoardMove> {
        val board = gameSnapshotState.board
        val square = board.find(this) ?: return emptyList()
        val moves = mutableListOf<BoardMove>()

        advanceSingle(board, square)?.let { moves += it }
        advanceTwoSquares(board, square)?.let { moves += it }
        captureDiagonalLeft(board, square)?.let { moves += it }
        captureDiagonalRight(board, square)?.let { moves += it }
        enPassantCaptureLeft(gameSnapshotState, square)?.let { moves += it }
        enPassantCaptureRight(gameSnapshotState, square)?.let { moves += it }

        return moves.flatMap {
            it.checkForPromotion()
        }
    }

    private fun advanceSingle(
        board: Board,
        square: Square
    ): BoardMove? {
        val deltaRank = if (setPiece == SetPiece.WHITE) 1 else -1
        val target = board[square.file, square.rank + deltaRank]
        return if (target?.isEmpty == true) BoardMove(
            move = Move(this, square.position, target.position)
        ) else null
    }

    private fun advanceTwoSquares(
        board: Board,
        square: Square
    ): BoardMove? {
        if ((setPiece == SetPiece.WHITE && square.rank == 2) || (setPiece == SetPiece.BLACK && square.rank == 7)) {
            val deltaRank1 = if (setPiece == SetPiece.WHITE) 1 else -1
            val deltaRank2 = if (setPiece == SetPiece.WHITE) 2 else -2
            val target1 = board[square.file, square.rank + deltaRank1]
            val target2 = board[square.file, square.rank + deltaRank2]
            return if (target1?.isEmpty == true && target2?.isEmpty == true) BoardMove(
                move = Move(this, square.position, target2.position)
            ) else null
        }
        return null
    }

    private fun captureDiagonalLeft(
        board: Board,
        square: Square
    ): BoardMove? = captureDiagonal(board, square, -1)

    private fun captureDiagonalRight(
        board: Board,
        square: Square
    ): BoardMove? = captureDiagonal(board, square, 1)

    private fun captureDiagonal(
        board: Board,
        square: Square,
        deltaFile: Int
    ): BoardMove? {
        val deltaRank = if (setPiece == SetPiece.WHITE) 1 else -1
        val target = board[square.file + deltaFile, square.rank + deltaRank]
        return if (target?.hasPiece(setPiece.change()) == true) BoardMove(
            move = Move(this, square.position, target.position),
            preMove = Capture(target.piece!!, target.position)
        ) else null
    }

    private fun enPassantCaptureLeft(
        gameSnapshotState: GameSnapshotState,
        square: Square
    ): BoardMove? = enPassantDiagonal(gameSnapshotState, square, -1)

    private fun enPassantCaptureRight(
        gameSnapshotState: GameSnapshotState,
        square: Square
    ): BoardMove? = enPassantDiagonal(gameSnapshotState, square, 1)

    private fun enPassantDiagonal(
        gameSnapshotState: GameSnapshotState,
        square: Square,
        deltaFile: Int
    ): BoardMove? {
        if (square.position.rank != if (setPiece == SetPiece.WHITE) 5 else 4) return null
        val lastMove = gameSnapshotState.lastMove ?: return null
        if (lastMove.piece !is Pawn) return null
        val fromInitialSquare = (lastMove.from.rank == if (setPiece == SetPiece.WHITE) 7 else 2)
        val twoSquareMove = (lastMove.to.rank == square.position.rank)
        val isOnNextFile = lastMove.to.file == square.file + deltaFile

        return if (fromInitialSquare && twoSquareMove && isOnNextFile) {
            val deltaRank = if (setPiece == SetPiece.WHITE) 1 else -1
            val enPassantTarget = gameSnapshotState.board[square.file + deltaFile, square.rank + deltaRank]
            val capturedPieceSquare = gameSnapshotState.board[square.file + deltaFile, square.rank]
            requireNotNull(enPassantTarget)
            requireNotNull(capturedPieceSquare)

            BoardMove(
                move = Move(this, square.position, enPassantTarget.position),
                preMove = Capture(capturedPieceSquare.piece!!, capturedPieceSquare.position)
            )
        } else null
    }
}




private fun BoardMove.checkForPromotion(): List<BoardMove> =
    if (move.to.rank == if (piece.setPiece == SetPiece.WHITE) 8 else 1) {
        listOf(
            copy(consequence = Promotion(move.to, Queen(piece.setPiece))),
            copy(consequence = Promotion(move.to, Rook(piece.setPiece))),
            copy(consequence = Promotion(move.to, Bishop(piece.setPiece))),
            copy(consequence = Promotion(move.to, Knight(piece.setPiece))),
        )
    } else listOf(this)