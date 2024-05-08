package com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz

import androidx.compose.ui.graphics.Color
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import kotlinx.parcelize.Parcelize


@Parcelize
object BlackKingsEscape : KingsEscapeSquares(
    set = SetPiece.BLACK,
    colorScale = Color.Unspecified to Color(0xBBEE6666)
) {
    override val name = R.string.change_name
}
