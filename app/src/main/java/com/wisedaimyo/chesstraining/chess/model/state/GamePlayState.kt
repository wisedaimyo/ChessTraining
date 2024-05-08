package com.wisedaimyo.chesstraining.chess.model.state

import android.os.Parcelable
import com.wisedaimyo.chesstraining.chess.model.dataviz.DatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.dataviz.None
import kotlinx.parcelize.Parcelize

@Parcelize
data class GamePlayState(
    val gameState: GameState = GameState(GameMetaInfo.createWithDefaults()),
    val uiState: UiState = UiState(gameState.currentSnapshotState),
    var promotionState: PromotionState = PromotionState.None,
    val visualisation: DatasetVisualisation = None
) : Parcelable