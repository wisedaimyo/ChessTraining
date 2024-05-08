package com.wisedaimyo.chesstraining.chess.ui.square.decoration

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.wisedaimyo.chesstraining.chess.model.board.Coordinate
import com.wisedaimyo.chesstraining.chess.ui.square.SquareDecoration
import com.wisedaimyo.chesstraining.chess.ui.square.SquareRenderProperties


open class SquarePositionLabelPacked(
    private val display: (Coordinate) -> Boolean,
) : SquareDecoration {

    @Composable
    override fun render(properties: SquareRenderProperties) {
        if (display(properties.coordinate)) {
            PositionLabel(
                text = properties.position.toString(),
                alignment = Alignment.TopStart,
                modifier = properties.sizeModifier
            )
        }
    }
}
