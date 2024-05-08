package com.wisedaimyo.chesstraining.chess.model.state

import com.wisedaimyo.chesstraining.chess.model.board.Position

data class MoveIntention(
    val from: Position,
    val to: Position
)