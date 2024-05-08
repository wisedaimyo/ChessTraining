package com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz

import androidx.compose.ui.graphics.Color
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.dataviz.Datapoint
import com.wisedaimyo.chesstraining.chess.model.dataviz.DatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import kotlinx.parcelize.Parcelize


@Parcelize
object BlockedPieces : DatasetVisualisation {

    override val name = R.string.app_name

    override val minValue: Int = 0

    override val maxValue: Int = 31

    override fun dataPointAt(
        position: Position,
        state: GameSnapshotState,
        cache: MutableMap<Any, Any>
    ): Datapoint? =
        valueAt(position, state)?.let { value ->
            Datapoint(
                value = value,
                label = null,
                colorScale = when (value) {
                    0 -> Color.Red.copy(alpha = 0.35f) to Color.Unspecified
                    else -> Color.Unspecified to Color.Unspecified
                },
            )
        }

    private fun valueAt(position: Position, state: GameSnapshotState): Int? =
        when {
            state.board[position].isEmpty -> null
            else -> state.legalMovesFrom(position).size
        }
}
