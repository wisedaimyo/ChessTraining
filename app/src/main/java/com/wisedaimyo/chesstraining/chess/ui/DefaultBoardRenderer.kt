package com.wisedaimyo.chesstraining.chess.ui

import com.wisedaimyo.chesstraining.chess.ui.decoration.DecoratedSquares
import com.wisedaimyo.chesstraining.chess.ui.pieces.Pieces
import com.wisedaimyo.chesstraining.chess.ui.square.DefaultSquareRenderer


object DefaultBoardRenderer : BoardRenderer {

    override val decorations: List<BoardDecoration> =
        listOf(
            DecoratedSquares(DefaultSquareRenderer.decorations),
            Pieces
        )
}
