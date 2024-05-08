package com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz

import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.dataviz.Datapoint
import com.wisedaimyo.chesstraining.chess.model.dataviz.DatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import kotlinx.parcelize.Parcelize


@Parcelize
object None : DatasetVisualisation {

    override val name = R.string.viz_none

    override val minValue: Int = Int.MIN_VALUE

    override val maxValue: Int = Int.MAX_VALUE

    override fun dataPointAt(
        position: Position,
        state: GameSnapshotState,
        cache: MutableMap<Any, Any>
    ): Datapoint? =
        null
}
