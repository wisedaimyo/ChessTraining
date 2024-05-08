package com.wisedaimyo.chesstraining.chess.model.state

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.move.AppliedMove
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    val gameMetaInfo: GameMetaInfo,
    val states: List<GameSnapshotState> = listOf(GameSnapshotState()),
    val currentIndex: Int = 0,
    val lastActiveState: GameSnapshotState = states.first(),
) : Parcelable {
    val hasPrevIndex: Boolean
        get() = currentIndex > 0

    val hasNextIndex: Boolean
        get() = currentIndex < states.lastIndex

    val currentSnapshotState: GameSnapshotState
        get() = states[currentIndex]

    val toMove: SetPiece
        get() = currentSnapshotState.toMove

    val resolution: GameProgress
        get() = currentSnapshotState.gameProgress

    fun moves(): List<AppliedMove> =
        states
            .map { gameState -> gameState.move }
            .filterNotNull()
}