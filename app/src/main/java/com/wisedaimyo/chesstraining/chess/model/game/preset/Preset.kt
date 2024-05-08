package com.wisedaimyo.chesstraining.chess.model.game.preset

import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.ui.pieces.Pieces

interface Preset {
    fun apply(gameController: GameController)
}