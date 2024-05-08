package com.wisedaimyo.chesstraining.chess.model.game

import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GameMetaInfo
import com.wisedaimyo.chesstraining.chess.model.state.GameProgress


fun GameMetaInfo.withResolution(resolution: GameProgress, lastMoveBy: SetPiece): GameMetaInfo =
    when (resolution) {
        GameProgress.IN_PROGRESS -> this
        GameProgress.CHECKMATE -> {
            val result = if (lastMoveBy == SetPiece.WHITE) "1-0" else "0-1"
            val winner = if (lastMoveBy == SetPiece.WHITE) white else black
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to result)
                    .plus(GameMetaInfo.KEY_TERMINATION to "$winner won by checkmate")
            )
        }
        GameProgress.STALEMATE -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "½ - ½")
                    .plus(GameMetaInfo.KEY_TERMINATION to "Stalemate")
            )
        }
        GameProgress.LOSE_PUZZLE -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "0")
                    .plus(GameMetaInfo.KEY_TERMINATION to "LOST PUZZLE")
            )
        }
        GameProgress.WIN_PUZZLE -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "0")
                    .plus(GameMetaInfo.KEY_TERMINATION to "WIN PUZZLE")
            )
        }
        GameProgress.DRAW_BY_REPETITION -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "½ - ½")
                    .plus(GameMetaInfo.KEY_TERMINATION to "Draw by repetition")
            )
        }
        GameProgress.RESIGN -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "-")
                    .plus(GameMetaInfo.KEY_TERMINATION to "Resignation")
            )
        }
        GameProgress.DRAW -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "½ - ½")
                    .plus(GameMetaInfo.KEY_TERMINATION to "Draw by offer")
            )
        }
        GameProgress.INSUFFICIENT_MATERIAL -> {
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to "½ - ½")
                    .plus(GameMetaInfo.KEY_TERMINATION to "Remiza nedostatok materialu")
            )
        }
        GameProgress.LOSE_ON_TIME -> {
            val result = if (lastMoveBy == SetPiece.WHITE) "1-0" else "0-1"
            val winner = if (lastMoveBy == SetPiece.WHITE) white else black
            copy(
                tags = tags
                    .plus(GameMetaInfo.KEY_RESULT to result)
                    .plus(GameMetaInfo.KEY_TERMINATION to "$winner won by time")
            )
        }


    }
