package com.wisedaimyo.chesstraining

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.Bishop
import com.wisedaimyo.chesstraining.chess.model.pieces.King
import com.wisedaimyo.chesstraining.chess.model.pieces.Knight
import com.wisedaimyo.chesstraining.chess.model.pieces.Piece
import com.wisedaimyo.chesstraining.chess.model.pieces.Queen
import com.wisedaimyo.chesstraining.chess.model.pieces.Rook
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.pieces.Star
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.ui.BoardWithEnable
import com.wisedaimyo.chesstraining.main.components.AnimatedBallToArrow
import com.wisedaimyo.chesstraining.main.components.BackButton
import kotlinx.coroutines.delay


@Preview
@Composable
fun testing() {
    val gamePlayState = rememberSaveable { mutableStateOf(GamePlayState()) }

    val pieces = arrayOf(
        King(SetPiece.WHITE),
        Queen(SetPiece.WHITE),
        Rook(SetPiece.WHITE),
        Bishop(SetPiece.WHITE),
        Knight(SetPiece.WHITE)
    )

    var newPositonPiece by rememberSaveable { mutableStateOf(Position.random()) }
    var newPositonStar by rememberSaveable { mutableStateOf(Position.random()) }
    var typePiece by rememberSaveable { mutableStateOf( pieces[0] ) }

    val view = LocalView.current

    var solved by rememberSaveable { mutableStateOf(0) }
    var currentPiece by rememberSaveable { mutableStateOf(0) }

    val gameController = remember {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it }
        ).apply {
            val board = com.wisedaimyo.chesstraining.chess.model.board.Board(
                pieces = mapOf<Position, Piece>(
                    Pair(newPositonStar, Star(SetPiece.BLACK)),
                    Pair(newPositonPiece, typePiece)
                )
            )
            reset(
                GameSnapshotState(
                    board = board,
                    toMove = SetPiece.WHITE
                )
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

        LaunchedEffect(newPositonPiece, newPositonStar) {
            if (newPositonStar == newPositonPiece) {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                solved += 1
                if(solved == 5 && currentPiece != 4) {
                    currentPiece += 1
                    typePiece = pieces[currentPiece]
                    solved = 0
                }
                delay(500)
                newPositonPiece = if(currentPiece == 3) Position.randomBlack() else Position.random()
                newPositonStar = if(currentPiece == 3) Position.randomBlack() else Position.random()
                gameController.apply {
                    val board = com.wisedaimyo.chesstraining.chess.model.board.Board(
                    pieces = mapOf<Position, Piece>(
                        Pair(newPositonStar, Star(SetPiece.BLACK)),
                        Pair(newPositonPiece, typePiece)
                    )
                )
                    reset(
                        GameSnapshotState(
                            board = board,
                            toMove = SetPiece.WHITE
                        )
                    ) }
            }
        }

        LaunchedEffect(key1 = gamePlayState.value) {

            if (gamePlayState.value.gameState.currentSnapshotState.lastMove != null) {
                newPositonPiece = gamePlayState.value.gameState.currentSnapshotState.lastMove!!.to

                gameController.apply {
                    reset(
                        GameSnapshotState(
                            board = com.wisedaimyo.chesstraining.chess.model.board.Board(
                                pieces = mapOf<Position, Piece>(
                                    Pair(newPositonStar, Star(SetPiece.BLACK)),
                                    Pair(newPositonPiece, typePiece)
                                )
                            ),
                            toMove = SetPiece.WHITE
                        )
                    )
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {

            BackButton {

            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {

                Text(
                    text = "Zober hviezdu",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = MaterialTheme.typography.displayMedium.fontSize,
                    color = Color.White
                )

                Spacer(modifier = Modifier.padding(vertical = 20.dp))

                BoardWithEnable(
                    gamePlayState = gamePlayState.value,
                    gameController = gameController,
                    isFlipped = false,
                    isBoardEnabled = if (newPositonStar == newPositonPiece) false else true
                )

                Spacer(modifier = Modifier.padding(vertical = 20.dp))

                Text(
                    text = "${solved}/5",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = MaterialTheme.typography.displayMedium.fontSize,
                    color = Color.Black
                )

            }
        }

        if(newPositonStar == newPositonPiece) {
            AnimatedBallToArrow()
        }

    }



}
