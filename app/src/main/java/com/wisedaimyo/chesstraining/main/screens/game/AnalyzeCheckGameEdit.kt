package com.wisedaimyo.chesstraining.main.screens.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.API.RetrofitInstance
import com.wisedaimyo.chesstraining.API.data.NextMoveImpl
import com.wisedaimyo.chesstraining.API.presentation.NextMoveViewModel
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.canCastle
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz.ActiveDatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.Pawn
import com.wisedaimyo.chesstraining.chess.model.pieces.Piece
import com.wisedaimyo.chesstraining.chess.model.pieces.Queen
import com.wisedaimyo.chesstraining.chess.model.pieces.Rook
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.PieceEffect
import com.wisedaimyo.chesstraining.chess.model.state.PromotionState
import com.wisedaimyo.chesstraining.chess.ui.Board
import com.wisedaimyo.chesstraining.chess.ui.pieces.Pieces
import com.wisedaimyo.chesstraining.extractMove2
import com.wisedaimyo.chesstraining.generateFEN
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.ChessEvaluationBar
import com.wisedaimyo.chesstraining.main.data.models.PLAYED_GAMES
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.AnalyzeCheckGameEditViewModel
import com.wisedaimyo.chesstraining.stringToList
import com.wisedaimyo.chesstraining.whoPlays
import kotlinx.coroutines.delay
import kotlin.random.Random

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AnalyzeCheckGameEdit(
    viewModel: AnalyzeCheckGameEditViewModel = hiltViewModel(),
    navController: NavController,
    id: String = "",
) {
    val isGameLoaded = remember { mutableStateOf(false) }
    if(id != "") {
        viewModel.getChosenGame(id)
        isGameLoaded.value = true
    }

    var currentTimeInMs by remember { mutableStateOf(System.currentTimeMillis()) }
    val evaluateVM = viewModel { NextMoveViewModel(NextMoveImpl(RetrofitInstance.api)) }
    val isFlipped = remember { mutableStateOf(Random.nextBoolean()) }
    var moves = remember { mutableStateOf("") }
    var fenNotation by rememberSaveable { mutableStateOf("") }
    var whoPlays by rememberSaveable { mutableStateOf("") }
    var currentPlayerColor by rememberSaveable { mutableStateOf("") }
    var gamePlayState = rememberSaveable { mutableStateOf(GamePlayState()) }
    var firstMove = remember { mutableStateOf(false) }

    val gameController = remember(gamePlayState.value) {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it },
            preset = null
        )
    }

    LaunchedEffect(key1 = gamePlayState.value.gameState.states.size) {
        moves.value = ""
        for (x in 0..<gamePlayState.value.gameState.states.size-1) {
            if(gamePlayState.value.gameState.states[x].move.toString() != "null")
                if(x%2==0) {
                    moves.value += "${(x/2)+1}. ${gamePlayState.value.gameState.states[x].move.toString()}"
                } else {
                    moves.value += gamePlayState.value.gameState.states[x].move.toString() + "  "
                }
        }
    }

    LaunchedEffect(key1 = viewModel.chosenGame.value) {
        viewModel.getPlayersForGame(viewModel.chosenGame.value)

        whoPlays = viewModel.chosenGame.value.fen?.let { whoPlays(it) }.toString()
        if (viewModel.chosenGame.value.white == Firebase.auth.uid)
            currentPlayerColor = "White"
        else
            currentPlayerColor = "Black"

        isFlipped.value = currentPlayerColor != "White"

        if (viewModel.chosenGame.value.moves != null)
            for (moveToExtract in viewModel.chosenGame.value.moves!!) {
                val move = extractMove2(moveToExtract)
                val fromPosition = Position.fromString(move?.first ?: "")
                val toPosition = Position.fromString(move?.second ?: "")
                if (move != null) {
                    if (!move.first.equals("")) {
                        gameController.applyMove(fromPosition, toPosition)
                    }
                }
            }
    }

    LaunchedEffect(key1 = gameController.getGamePlayState, key2 = currentTimeInMs, key3 = evaluateVM.nextMove) {
            fenNotation = ""

            gamePlayState.value.gameState.lastActiveState.board.pieces.forEach { entry ->
                if (entry.value.setPiece.toString().equals("BLACK")) {
                    fenNotation += entry.key.name
                    fenNotation += entry.value.textSymbol.lowercase() + " "
                } else {
                    fenNotation += entry.key.name.uppercase()
                    fenNotation += entry.value.textSymbol.uppercase() + " "
                }
            }

            var listOfMoves = mutableListOf<Pair<String, String>>()
            for (state in gamePlayState.value.gameState.states) {
                listOfMoves.add(
                    Pair(
                        "${state.move?.move?.from?.name}",
                        "${state.move?.move?.to?.name}"
                    )
                )
            }

            fenNotation = fenNotation.substring(0, fenNotation.length - 1)

            val list = stringToList(fenNotation)
            if (gamePlayState.value.gameState.currentSnapshotState.lastMove?.move?.piece?.value == 1)
                fenNotation = generateFEN(
                    list,
                    castling = canCastle(listOfMoves),
                    enpassant = gamePlayState.value.gameState.currentSnapshotState.lastMove?.move?.to.toString(),
                    whiteMoves = gameController.toMove != SetPiece.BLACK
                )
            else
                fenNotation = generateFEN(
                    list,
                    castling = canCastle(listOfMoves),
                    whiteMoves = gameController.toMove != SetPiece.BLACK
                )

            evaluateVM.getNextMove(fenNotation, 13)
            evaluateVM.getEvaluation(fenNotation)

            delay(3000)
            currentTimeInMs = System.currentTimeMillis()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {
        Column {
            BackButton {
                navController.navigate(PLAYED_GAMES)
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Board(
                        gamePlayState = gamePlayState.value,
                        gameController = gameController,
                        isFlipped = isFlipped.value
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(5.dp)
                            .background(colorResource(id = R.color.primary))
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = moves.value,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                            )
                    }


                }

                Row (
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(text = "Najlepší ťah: ${evaluateVM.fromTo.value.first} na ${evaluateVM.fromTo.value.second}",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                        )
                }
                ChessEvaluationBar(
                    evaluation = evaluateVM.evaluation,
                    leadingSide = if(isFlipped.value) "black" else "white")


                AnalyzeCheckGameBottomMenu(gameController = gameController, isFlipped, isGameLoaded)
            }
        }
    }
}

@Composable
fun AnalyzeCheckGameBottomMenu(gameController: GameController,
                               isFlipped: MutableState<Boolean>,
                               isGameLoaded: MutableState<Boolean>
) {
    val showDialog = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(vertical = 15.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(imageVector = Icons.Default.Menu,
            contentDescription = "Backward",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    showDialog.value = true
                })
        )

        Image(
            painter = if(isFlipped.value) painterResource(id = R.drawable.king_black) else painterResource(id =R.drawable.king_white),
            contentDescription = "",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    isFlipped.value = !isFlipped.value
                }
                )
        )

        Icon(imageVector = Icons.Default.ArrowBack,
            contentDescription = "Backward",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    gameController.stepBackward()
                })
        )

        Icon(imageVector = Icons.Default.ArrowForward,
            contentDescription = "Forward",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    gameController.stepForward()
                })
        )

        if (showDialog.value) {
            DialogAnalyzeGameMenu(showDialog, isGameLoaded)
        }
    }
}


@Composable
fun DialogAnalyzeGameMenu(showDialog: MutableState<Boolean>, isGameLoaded: MutableState<Boolean>) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(text = "Menu",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                    )
            },
            text = {
                Text(text = "Zoznam možností.")
            },
            buttons = {
                Column(
                    modifier = Modifier.padding(all = 8.dp)
                ) {
                    if(isGameLoaded.value) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                // TODO ->  Odosli trenerovi

                            }
                        ) {
                            Text("Odoslať trénerovi")
                        }
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showDialog.value = false }
                    ) {
                        Text("Zruš okno")
                    }
                }
            }
        )
}



@Preview
@Composable
fun AnalyzeCheckGame_Preview() {
    AnalyzeCheckGameEdit(navController = rememberNavController())
}