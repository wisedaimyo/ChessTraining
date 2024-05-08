package com.wisedaimyo.chesstraining.chess.model.state

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.board.Board
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RepetitionRelevantState(
    val board: Board,
    val toMove: SetPiece,
    val castlingInfo: CastlingInfo,
) : Parcelable
