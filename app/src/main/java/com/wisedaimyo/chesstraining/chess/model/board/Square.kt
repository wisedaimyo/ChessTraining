package com.wisedaimyo.chesstraining.chess.model.board

import com.wisedaimyo.chesstraining.chess.model.pieces.Piece
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece

data class Square(
    val position: Position,
    val piece: Piece? = null
) {
    val file: Int = position.file
    val rank: Int = position.rank
    val isDark: Boolean = position.isDarkSquare()
    val isEmpty: Boolean = piece == null
    val isNotEmpty: Boolean = !isEmpty

    fun hasPiece(setPiece: SetPiece): Boolean = piece?.setPiece == setPiece
    val hasWhitePiece: Boolean = piece?.setPiece == SetPiece.WHITE
    val hasBlackPiece: Boolean = piece?.setPiece == SetPiece.BLACK

    override fun toString(): String {
       return ChessFile.values()[file - 1].toString() + rank.toString()
    }

}