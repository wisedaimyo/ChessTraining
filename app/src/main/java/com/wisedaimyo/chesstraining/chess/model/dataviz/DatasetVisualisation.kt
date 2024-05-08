package com.wisedaimyo.chesstraining.chess.model.dataviz

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState

interface DatasetVisualisation : Parcelable {

    val name: Int

    val minValue: Int

    val maxValue: Int

    fun dataPointAt(position: Position, state: GameSnapshotState, cache: MutableMap<Any, Any>): Datapoint?
}
