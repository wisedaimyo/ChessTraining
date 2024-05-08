package com.wisedaimyo.chesstraining.chess.ui

import androidx.compose.ui.unit.Dp
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.model.state.UiState


data class BoardRenderProperties(
    val fromState: GameSnapshotState,
    val toState: GameSnapshotState,
    val uiState: UiState,
    val isFlipped: Boolean,
    val squareSize: Dp,
    val onClick: (Position) -> Unit,
) {
    val cache: MutableMap<Any, Any> = mutableMapOf()
}
