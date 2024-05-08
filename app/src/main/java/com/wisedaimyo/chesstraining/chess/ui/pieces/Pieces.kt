package com.wisedaimyo.chesstraining.chess.ui.pieces

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.wisedaimyo.chesstraining.chess.model.board.toCoordinate
import com.wisedaimyo.chesstraining.chess.model.board.toModifier
import com.wisedaimyo.chesstraining.chess.model.board.toOffset
import com.wisedaimyo.chesstraining.chess.ui.BoardDecoration
import com.wisedaimyo.chesstraining.chess.ui.BoardPreview
import com.wisedaimyo.chesstraining.chess.ui.BoardRenderProperties

object Pieces : BoardDecoration {

    @Composable
    override fun render(properties: BoardRenderProperties) {
        properties.toState.board.pieces.forEach { (toPosition, piece) ->
            key(piece) {
                val fromPosition = properties.fromState.board.find(piece)?.position
                val currentOffset = fromPosition
                    ?.toCoordinate(properties.isFlipped)
                    ?.toOffset(properties.squareSize)

                val targetOffset = toPosition
                    .toCoordinate(properties.isFlipped)
                    .toOffset(properties.squareSize)

                val offset = remember { Animatable(currentOffset ?: targetOffset, Offset.VectorConverter) }
                LaunchedEffect(targetOffset) {
                    offset.animateTo(targetOffset, tween(100, easing = LinearEasing))
                }
                LaunchedEffect(properties.isFlipped) {
                    offset.snapTo(targetOffset)
                }

                Piece(
                    piece = piece,
                    squareSize = properties.squareSize,
                    modifier = offset.value.toModifier()
                )
            }
        }
    }

}

@Preview
@Composable
fun PiecesPreview() {
    BoardPreview()
}
