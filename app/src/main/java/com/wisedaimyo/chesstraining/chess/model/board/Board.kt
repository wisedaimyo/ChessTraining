package com.wisedaimyo.chesstraining.chess.model.board

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.pieces.Piece
import com.wisedaimyo.chesstraining.chess.model.board.Position.*
import com.wisedaimyo.chesstraining.chess.model.pieces.*
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece.*
import com.wisedaimyo.chesstraining.chess.model.state.PieceEffect
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class Board(
    val pieces: Map<Position, Piece>
) : Parcelable {
    constructor() : this(
        pieces = initialPieces
    )

    @IgnoredOnParcel
    val squares = Position.entries.map { position ->
        position to Square(position, pieces[position])
    }.toMap()

    operator fun get(position: Position): Square =
        squares[position]!!

    operator fun get(file: ChessFile, rank: Int): Square? =
        get(file.ordinal + 1, rank)

    operator fun get(file: Int, rank: Int): Square? {
        return try {
            val position = Position.from(file, rank)
            squares[position]
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun find(piece: Piece): Square? =
        squares.values.firstOrNull { it.piece == piece }

    inline fun <reified T : Piece> find(setPiece: SetPiece): List<Square> =
        squares.values.filter {
            it.piece != null &&
                    it.piece::class == T::class &&
                    it.piece.setPiece == setPiece
        }

    fun pieces(set: SetPiece): Map<Position, Piece> =
        pieces.filter { (_, piece) -> piece.setPiece == set }

    fun apply(effect: PieceEffect?): Board =
        effect?.applyOn(this) ?: this
}

private val initialPieces = mapOf(
    a8 to Rook(BLACK),
    b8 to Knight(BLACK),
    c8 to Bishop(BLACK),
    d8 to Queen(BLACK),
    e8 to King(BLACK),
    f8 to Bishop(BLACK),
    g8 to Knight(BLACK),
    h8 to Rook(BLACK),

    a7 to Pawn(BLACK),
    b7 to Pawn(BLACK),
    c7 to Pawn(BLACK),
    d7 to Pawn(BLACK),
    e7 to Pawn(BLACK),
    f7 to Pawn(BLACK),
    g7 to Pawn(BLACK),
    h7 to Pawn(BLACK),

    a2 to Pawn(WHITE),
    b2 to Pawn(WHITE),
    c2 to Pawn(WHITE),
    d2 to Pawn(WHITE),
    e2 to Pawn(WHITE),
    f2 to Pawn(WHITE),
    g2 to Pawn(WHITE),
    h2 to Pawn(WHITE),

    a1 to Rook(WHITE),
    b1 to Knight(WHITE),
    c1 to Bishop(WHITE),
    d1 to Queen(WHITE),
    e1 to King(WHITE),
    f1 to Bishop(WHITE),
    g1 to Knight(WHITE),
    h1 to Rook(WHITE),
)
