package com.wisedaimyo.chesstraining.chess.ui

import androidx.compose.runtime.Composable
import com.wisedaimyo.chesstraining.chess.model.game.preset.Preset
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState

@Composable
fun Preset(preset: Preset) {
    Game(
        state = GamePlayState(),
        preset = preset
    )
}