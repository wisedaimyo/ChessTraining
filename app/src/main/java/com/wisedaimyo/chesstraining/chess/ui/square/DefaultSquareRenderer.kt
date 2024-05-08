package com.wisedaimyo.chesstraining.chess.ui.square

import com.wisedaimyo.chesstraining.chess.ui.square.decoration.DatasetVisualiser
import com.wisedaimyo.chesstraining.chess.ui.square.decoration.DefaultHighlightSquare
import com.wisedaimyo.chesstraining.chess.ui.square.decoration.DefaultSquareBackground
import com.wisedaimyo.chesstraining.chess.ui.square.decoration.DefaultSquarePositionLabel
import com.wisedaimyo.chesstraining.chess.ui.square.decoration.TargetMarks


object DefaultSquareRenderer : SquareRenderer {

    override val decorations: List<SquareDecoration> =
        listOf(
            DefaultSquareBackground,
            DefaultHighlightSquare,
            DefaultSquarePositionLabel,
            DatasetVisualiser,
            TargetMarks
        )
}
