package com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz

import androidx.compose.runtime.compositionLocalOf
import com.wisedaimyo.chesstraining.chess.model.dataviz.DatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.dataviz.None

val datasetVisualisations = listOf(
    None,
    ActivePieces,
    BlockedPieces,
    Influence,
    BlackKingsEscape,
    WhiteKingsEscape,
    KnightsMoveCount,
    CheckmateCount
)

val ActiveDatasetVisualisation = compositionLocalOf<DatasetVisualisation> { None }