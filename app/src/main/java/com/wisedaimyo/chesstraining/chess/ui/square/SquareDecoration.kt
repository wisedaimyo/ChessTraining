package com.wisedaimyo.chesstraining.chess.ui.square

import androidx.compose.runtime.Composable

interface SquareDecoration {

    @Composable
    fun render(properties: SquareRenderProperties)
}

