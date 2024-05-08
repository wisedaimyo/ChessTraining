package com.wisedaimyo.chesstraining.chess.model.dataviz

import androidx.compose.ui.graphics.Color

data class Datapoint(
    val value: Int?,
    val label: String?,
    val colorScale: Pair<Color, Color>,
)