package com.wisedaimyo.chesstraining.chess.model.board

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GameProgress
import com.wisedaimyo.chesstraining.chess.model.state.GameState

fun idx(file: Int, rank: Int): Int =
    (file - 1) * 8 + (rank - 1)

fun validate(file: Int, rank: Int) {
    require(file >= 1)
    require(file <= 8)
    require(rank >= 1)
    require(rank <= 8)
}

fun Position.isLightSquare(): Boolean =
    (ordinal + file % 2) % 2 == 0

fun Position.isDarkSquare(): Boolean =
    (ordinal + file % 2) % 2 == 1

fun Position.toCoordinate(isFlipped: Boolean): Coordinate = if (isFlipped)
    Coordinate(
        x = Coordinate.max.x - file + 1,
        y = rank.toFloat(),
    ) else Coordinate(
    x = file.toFloat(),
    y = Coordinate.max.y - rank + 1,
)


fun Coordinate.toOffset(squareSize: Dp): Offset =
    Offset(
        x = (x - 1) * squareSize.value,
        y = (y - 1) * squareSize.value
    )


fun Coordinate.toOffsetModifier(squareSize: Dp): Modifier =
    Modifier
        .offset(
            Dp((x - 1) * squareSize.value),
            Dp((y - 1) * squareSize.value)
        )

fun Offset.toModifier(): Modifier =
    Modifier.offset(Dp(x), Dp(y))

fun GameState.resolutionText(): Comparable<*> =
    when (resolution) {
        GameProgress.IN_PROGRESS -> when (toMove) {
            SetPiece.WHITE -> R.string.app_name
            SetPiece.BLACK -> R.string.app_name
        }
        GameProgress.CHECKMATE -> when (toMove) {
            SetPiece.WHITE -> R.string.app_name
            SetPiece.BLACK -> R.string.app_name
        }
        GameProgress.RESIGN -> "Hrac sa vzdal"
        GameProgress.DRAW -> "Bola ponuknuta remiza"
        GameProgress.STALEMATE -> R.string.app_name
        GameProgress.DRAW_BY_REPETITION -> R.string.app_name
        GameProgress.INSUFFICIENT_MATERIAL -> R.string.app_name
        GameProgress.LOSE_ON_TIME -> "PREHRA NA CAS"
        GameProgress.LOSE_PUZZLE -> "PREHRA PUZZLE"
        GameProgress.WIN_PUZZLE -> "VYHRA PUZZLE"
    }
