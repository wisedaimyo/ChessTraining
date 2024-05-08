package com.wisedaimyo.chesstraining.chess.model.pieces

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState

interface Piece: Parcelable {

    val setPiece: SetPiece

    val asset: Int?

    val symbol: String

    val textSymbol: String

    val value: Int

    fun pseudoLegalMoves(gameSnapshotState: GameSnapshotState, checkCheck: Boolean): List<BoardMove> = emptyList()
}