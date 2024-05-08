package com.wisedaimyo.chesstraining.chess.model.state

import com.wisedaimyo.chesstraining.chess.model.move.AppliedMove

data class GameStateTransition(
    val fromSnapshotState: GameSnapshotState,
    val toSnapshotState: GameSnapshotState,
    val move: AppliedMove
)