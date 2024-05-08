package com.wisedaimyo.chesstraining.chess.model.game.preset

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.wisedaimyo.chesstraining.chess.model.board.Board
import com.wisedaimyo.chesstraining.chess.model.board.Position.*
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.King
import com.wisedaimyo.chesstraining.chess.model.pieces.Knight
import com.wisedaimyo.chesstraining.chess.model.pieces.Pawn
import com.wisedaimyo.chesstraining.chess.model.pieces.Queen
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece.*
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.ui.Board
import com.wisedaimyo.chesstraining.getPiece
import com.wisedaimyo.chesstraining.main.ui.theme.ChessTrainingTheme

@Preview(showBackground = true)
@Composable
fun PromotionPresetPreview() {
    Column {

        val gamePlayState = rememberSaveable { mutableStateOf(GamePlayState()) }
        val gameController = remember(gamePlayState.value) {
            GameController(
                getGamePlayState = { gamePlayState.value },
                setGamePlayState = { gamePlayState.value = it },
                preset = null
            )
        }
        gameController.apply {
            val board = Board(
                pieces = mapOf(
                    a7 to King(BLACK),
                    a2 to Pawn(BLACK),
                    f8 to Knight(BLACK),
                    g2 to King(WHITE),
                    g7 to Pawn(WHITE),
                )
            )
            reset(
                GameSnapshotState(
                    board = board,
                    toMove = BLACK
                )
            )
        }

        gameController.apply {
            applyMove(a2,a1)
            onPromotionPieceSelected(getPiece('b'))
            applyMove(a2,a1)
        }

        //gamePlayState.value.gameState.currentSnapshotState.toMove.change()

        Board(
            gamePlayState = gamePlayState.value,
            gameController = gameController
        )


    }

}