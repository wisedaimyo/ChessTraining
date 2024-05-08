package com.wisedaimyo.chesstraining.chess.model.state

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.pieces.Piece
import kotlinx.parcelize.Parcelize


sealed class PromotionState : Parcelable {
    @Parcelize
    object None : PromotionState()

    @Parcelize
    data class Await(val position: Position) : PromotionState()

    @Parcelize
    data class ContinueWith(val piece: Piece) : PromotionState()
}
