package com.wisedaimyo.chesstraining.chess.ui.square.decoration

import com.wisedaimyo.chesstraining.chess.model.board.Coordinate

object DefaultSquarePositionLabel : SquarePositionLabelSplit(
    displayFile = { coordinate -> coordinate.y == Coordinate.max.y },
    displayRank = { coordinate -> coordinate.x == Coordinate.min.x }
)
