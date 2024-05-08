package com.wisedaimyo.chesstraining.chess.ui.square

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import com.wisedaimyo.chesstraining.chess.model.board.Coordinate
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.board.toCoordinate
import com.wisedaimyo.chesstraining.chess.ui.BoardRenderProperties


data class SquareRenderProperties(
    val position: Position,
    val isHighlighted: Boolean,
    val clickable: Boolean,
    val onClick: () -> Unit,
    val isPossibleMoveWithoutCapture: Boolean,
    val isPossibleCapture: Boolean,
    val boardProperties: BoardRenderProperties
) {
    val coordinate: Coordinate =
        position.toCoordinate(boardProperties.isFlipped)

    val sizeModifier: Modifier
        get() = Modifier.size(boardProperties.squareSize)
}
