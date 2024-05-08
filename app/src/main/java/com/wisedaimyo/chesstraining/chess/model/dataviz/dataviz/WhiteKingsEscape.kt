package com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz

import androidx.compose.ui.graphics.Color
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import kotlinx.parcelize.Parcelize


@Parcelize
object WhiteKingsEscape : KingsEscapeSquares(
    set = SetPiece.WHITE,
    colorScale = Color.Unspecified to Color(0xBB6666EE)
) {
    override val name = R.string.app_name
}
