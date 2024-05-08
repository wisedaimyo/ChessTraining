package com.wisedaimyo.chesstraining.chess.model.move

import com.wisedaimyo.chesstraining.chess.model.board.Position

fun List<BoardMove>.targetPositions(): List<Position> =
    map { it.to }
