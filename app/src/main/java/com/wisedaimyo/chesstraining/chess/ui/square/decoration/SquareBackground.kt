package com.wisedaimyo.chesstraining.chess.ui.square.decoration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wisedaimyo.chesstraining.chess.model.board.isDarkSquare
import com.wisedaimyo.chesstraining.chess.ui.square.SquareDecoration
import com.wisedaimyo.chesstraining.chess.ui.square.SquareRenderProperties

open class SquareBackground(
    private val lightSquareColor: Color,
    private val darkSquareColor: Color,
) : SquareDecoration {

    @Composable
    override fun render(properties: SquareRenderProperties) {
        Canvas(properties.sizeModifier) {
            drawRect(color = if (properties.position.isDarkSquare()) Color(119,153,84) else Color(233,237,204))
        }
    }
}

