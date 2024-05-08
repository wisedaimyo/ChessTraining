package com.wisedaimyo.chesstraining.chess.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.model.state.UiState
import com.wisedaimyo.chesstraining.chess.model.board.Position.*
import com.wisedaimyo.chesstraining.chess.model.pieces.Bishop
import com.wisedaimyo.chesstraining.chess.model.pieces.Knight
import com.wisedaimyo.chesstraining.chess.model.pieces.Queen
import com.wisedaimyo.chesstraining.chess.model.pieces.Rook
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.PromotionState
import com.wisedaimyo.chesstraining.getDrawableName
import com.wisedaimyo.chesstraining.main.data.models.LEARN_MOVE_PIECES
import com.wisedaimyo.chesstraining.main.screens.main.MyGrid
import okhttp3.internal.wait
import kotlin.random.Random

@Composable
fun Board(
    gamePlayState: GamePlayState,
    gameController: GameController,
    isFlipped: Boolean = false,
) {
    var isNotFirst by remember { mutableStateOf(false) }
    LaunchedEffect(
        key1 = gamePlayState.promotionState
    ) {
        if(isNotFirst)
            if (gamePlayState.gameState.currentSnapshotState.toMove == SetPiece.WHITE)
                gamePlayState.promotionState = PromotionState.ContinueWith(
                    Queen(SetPiece.WHITE)
                )
            else
                gamePlayState.promotionState = PromotionState.ContinueWith(
                    Queen(SetPiece.BLACK)
                )

        isNotFirst = true
    }

    Board(
        fromState = gamePlayState.gameState.lastActiveState,
        toState = gamePlayState.gameState.currentSnapshotState,
        uiState = gamePlayState.uiState,
        isFlipped = isFlipped,
        onClick = { position -> gameController.onClick(position) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardWithEnable(
    gamePlayState: GamePlayState,
    gameController: GameController,
    isFlipped: Boolean = false,
    isBoardEnabled: Boolean = true
) {
    var isNotFirst by remember { mutableStateOf(false) }
    var isSheet by remember { mutableStateOf(false) }
    val currentPiece = remember { mutableStateOf("queen_black") }
    val context = LocalContext.current
    val movePieces = listOf(
        R.drawable.queen_black,
        R.drawable.rook_black,
        R.drawable.knight_black,
        R.drawable.bishop_black
    )

    LaunchedEffect(
        key1 = gamePlayState.promotionState
    ) {
        if(isNotFirst) {
            if (gamePlayState.gameState.currentSnapshotState.toMove == SetPiece.WHITE)
                gamePlayState.promotionState = PromotionState.ContinueWith(
                    if(currentPiece.value == "queen_black")
                      Queen(SetPiece.WHITE)
                    else if(currentPiece.value == "knight_black")
                        Knight(SetPiece.WHITE)
                    else if(currentPiece.value == "rook_black")
                        Rook(SetPiece.WHITE)
                    else if(currentPiece.value == "bishop_black")
                        Bishop(SetPiece.WHITE)
                    else
                        Queen(SetPiece.WHITE)
                )
            else
                gamePlayState.promotionState = PromotionState.ContinueWith(
                    if(currentPiece.value == "queen_black")
                        Queen(SetPiece.BLACK)
                    else if(currentPiece.value == "knight_black")
                        Knight(SetPiece.BLACK)
                    else if(currentPiece.value == "rook_black")
                        Rook(SetPiece.BLACK)
                    else if(currentPiece.value == "bishop_black")
                        Bishop(SetPiece.BLACK)
                    else
                        Queen(SetPiece.BLACK)
                )
        }
        isNotFirst = true
    }

    if(isSheet) {
        ModalBottomSheet(onDismissRequest = {
            isSheet = false
        }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp).padding(bottom = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2)
                ) {
                    items(movePieces.size) { item ->
                        Card(
                            modifier = Modifier
                                .clickable(onClick = {
                                    currentPiece.value = getDrawableName(context, movePieces[item]) ?: "queen_black"
                                    isSheet = false
                                })
                                .background(
                                    Color(
                                        red = Random.nextInt(0, 255),
                                        green = Random.nextInt(0, 255),
                                        blue = Random.nextInt(0, 255)
                                    )
                                )
                                .padding(45.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .size(64.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = movePieces[item] ),
                                    contentDescription = "Piece",
                                    modifier = Modifier
                                        .size(70.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Board(
            fromState = gamePlayState.gameState.lastActiveState,
            toState = gamePlayState.gameState.currentSnapshotState,
            uiState = gamePlayState.uiState,
            isFlipped = isFlipped,
            onClick = { position -> if (isBoardEnabled) gameController.onClick(position) }
        )
        Button(
            onClick = { isSheet = true }) {
            Text(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                text = "Vyber promóciu")
        }
    }
}

@Composable
fun Board(
    fromState: GameSnapshotState,
    toState: GameSnapshotState,
    uiState: UiState,
    isFlipped: Boolean = false,
    onClick: (Position) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val boardProperties =
            BoardRenderProperties(
                fromState = fromState,
                toState = toState,
                uiState = uiState,
                squareSize = maxWidth / 8,
                isFlipped = isFlipped,
                onClick = onClick
            )

        DefaultBoardRenderer.decorations.forEach {
            it.render(properties = boardProperties)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BoardPreview() {
        var gamePlayState = GamePlayState()
        val gameController = GameController({ gamePlayState }, { gamePlayState = it }).apply {
            applyMove(d2, d4)
            applyMove(e7, e5)
            applyMove(d4, e5)
            applyMove(f7, f6)
        }

    Column {
        Board(
            gameController = gameController,
            gamePlayState = gamePlayState
        )
    }
}

