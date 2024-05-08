package com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz

import androidx.compose.ui.graphics.Color
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.dataviz.Datapoint
import com.wisedaimyo.chesstraining.chess.model.dataviz.DatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import kotlinx.parcelize.Parcelize


@Parcelize
object ActivePieces : DatasetVisualisation {

    override val name = R.string.app_name

    override val minValue: Int = 2

    override val maxValue: Int = 10

    override fun dataPointAt(
        position: Position,
        state: GameSnapshotState,
        cache: MutableMap<Any, Any>
    ): Datapoint? =
        valueAt(position, state)?.let { value ->
            Datapoint(
                value = value,
                label = null,
                colorScale = Color.Green.copy(alpha = 0.025f) to Color.Green.copy(alpha = 0.85f)
            )
        }

    private fun valueAt(position: Position, state: GameSnapshotState): Int? =
        when {
            state.board[position].isEmpty -> null
            else -> state.legalMovesFrom(position).size
        }
}
