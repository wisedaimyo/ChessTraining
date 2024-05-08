package com.wisedaimyo.chesstraining.chess.ui

import androidx.compose.runtime.Composable

interface BoardDecoration {

    @Composable
    fun render(properties: BoardRenderProperties)
}
