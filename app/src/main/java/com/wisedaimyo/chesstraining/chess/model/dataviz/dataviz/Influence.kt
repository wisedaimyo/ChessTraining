package com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz

import androidx.compose.ui.graphics.Color
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.dataviz.Datapoint
import com.wisedaimyo.chesstraining.chess.model.dataviz.DatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import kotlinx.parcelize.Parcelize


@Parcelize
object Influence : DatasetVisualisation {

    override val name = R.string.app_name

    override val minValue: Int = -5

    override val maxValue: Int = 5

    private val redScale = Color.Red.copy(alpha = 0.5f) to Color.Transparent

    private val blueScale = Color.Transparent to Color.Blue.copy(alpha = 0.5f)

    override fun dataPointAt(
        position: Position,
        state: GameSnapshotState,
        cache: MutableMap<Any, Any>
    ): Datapoint? {
        val square = state.board[position]
        val legalMovesTo = state.legalMovesTo(position)
        if (legalMovesTo.isEmpty() && square.isNotEmpty) {
            return Datapoint(
                value = if (square.hasPiece(SetPiece.WHITE)) 1 else -1,
                label = null,
                colorScale = if (square.hasPiece(SetPiece.WHITE)) blueScale else redScale,
            )
        }

        val sum = legalMovesTo
            .map { if (it.piece.setPiece == SetPiece.WHITE) 1 else -1 }
            .sum()

        return Datapoint(
            value = sum,
            label = sum.toString(),
            colorScale = when {
                sum > 0 -> blueScale
                sum < 0 -> redScale
                else -> Color.Transparent to Color.Transparent
            }
        )
    }
}
