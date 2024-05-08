package com.wisedaimyo.chesstraining.main.screens.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Board
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.ui.BoardWithEnable
import com.wisedaimyo.chesstraining.extractMove
import com.wisedaimyo.chesstraining.getPiece
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.data.models.NAV_HOME
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.ChessCourse
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.ChessCourseViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.TrainerTasksViewModel
import com.wisedaimyo.chesstraining.parseFEN
import com.wisedaimyo.chesstraining.whoPlays

@SuppressLint("SuspiciousIndentation")
@Composable
fun PlayingScreen(
    viewModelChessCourse: TrainerTasksViewModel = hiltViewModel(),
    id: String = "",
    isFromTrainer: Boolean = false,
    viewModel: ChessCourseViewModel = hiltViewModel(),
    name: String = "",
    navController: NavController,
) {
    val isFlipped = remember { mutableStateOf(false) }
    var isLoaded by rememberSaveable { mutableStateOf(false) }
    val isFinished = rememberSaveable { mutableStateOf(false) }
    val description = rememberSaveable { mutableStateOf("") }

    val moves = rememberSaveable { mutableStateOf("") }
    val move = rememberSaveable { mutableIntStateOf(-1) }

    val chessGame = remember { mutableStateOf(ChessCourse()) }
    val chessGameTrainer = remember { mutableStateOf(TrainerChessTask()) }

    val scrollValue = rememberScrollState()

    val gamePlayState = rememberSaveable { mutableStateOf( GamePlayState()) }
    val gameController = remember(gamePlayState.value) {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it },
            preset = null
        )
    }

    LaunchedEffect(key1 = viewModelChessCourse.chessTaskResponse) {
        when (viewModelChessCourse.chessTaskResponse) {
            is Response.Success -> {

                Firebase.auth.uid?.let { viewModelChessCourse.isFinishedChessTask(id, it) }

                chessGameTrainer.value = (viewModelChessCourse.chessTaskResponse as Response.Success<TrainerChessTask>).data!!

                if(chessGameTrainer.value.description != null) description.value = chessGameTrainer.value.description!!

                if (chessGameTrainer.value.fen != null) {
                    gameController.apply {
                        val board = Board(
                            pieces = parseFEN(chessGameTrainer.value.fen!!)
                        )
                        reset(
                            GameSnapshotState(
                                board = board,
                                toMove = if (whoPlays(chessGameTrainer.value.fen!!) == "White") SetPiece.WHITE else SetPiece.BLACK
                            )
                        )
                    }
                }

                if (!isLoaded) {
                    for(moveToExtract in chessGameTrainer.value.moves!!) {
                        val move = extractMove(moveToExtract)
                        val fromPosition = Position.fromString(move?.first ?: "")
                        val toPosition = Position.fromString(move?.second ?: "")
                        val promotion = move?.third


                        if (move != null) {
                            if (promotion != null) {
                                if (promotion.isNotEmpty()) {
                                    gameController.apply {
                                        gameController.applyMove(
                                            fromPosition,
                                            toPosition
                                        )
                                        gameController.apply {
                                            onPromotionPieceSelected(getPiece(promotion[0]))
                                        }
                                        gameController.applyMove(
                                            fromPosition,
                                            toPosition
                                        )
                                    }
                                } else {
                                    if (!move.first.equals("")) {
                                        gameController.applyMove(
                                            fromPosition,
                                            toPosition
                                        )
                                    }
                                }
                            }
                        }
                    }

                    for (movex in 0..<chessGameTrainer.value.moves!!.size) {
                        gameController.stepBackward()
                    }
                    isLoaded = true
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(key1 = viewModel.getAllCoursesResponse) {
        if(name.isEmpty())
            viewModelChessCourse.getTrainerChessTask(id)

        if(name.isNotEmpty())
        when (viewModel.getAllCoursesResponse) {
            is Response.Success -> {
                chessGame.value =
                    (viewModel.getAllCoursesResponse as Response.Success<List<ChessCourse>>).data?.first { it.name == name }
                        ?: ChessCourse()

                if(chessGame.value.description != null) description.value = chessGame.value.description!!

                if (chessGame.value.fen != null) {
                    gameController.apply {
                        val board = Board(
                            pieces = parseFEN(chessGame.value.fen!!)
                        )
                        reset(
                            GameSnapshotState(
                                board = board,
                                toMove = if (whoPlays(chessGame.value.fen!!) == "White") SetPiece.WHITE else SetPiece.BLACK
                            )
                        )
                    }
                }

                if (!isLoaded) {
                    for(moveToExtract in chessGame.value.moves!!) {
                        val move = extractMove(moveToExtract)
                        val fromPosition = Position.fromString(move?.first ?: "")
                        val toPosition = Position.fromString(move?.second ?: "")
                        val promotion = move?.third


                        if (move != null) {
                            if (promotion != null) {
                                if (promotion.isNotEmpty()) {
                                    gameController.apply {
                                        gameController.applyMove(
                                            fromPosition,
                                            toPosition
                                        )
                                        gameController.apply {
                                            onPromotionPieceSelected(getPiece(promotion[0]))
                                        }
                                        gameController.applyMove(
                                            fromPosition,
                                            toPosition
                                        )
                                    }
                                } else {
                                    if (!move.first.equals("")) {
                                        gameController.applyMove(
                                            fromPosition,
                                            toPosition
                                        )
                                    }
                                }
                            }
                        }
                    }

                    for (movex in 0..<chessGame.value.moves!!.size) {
                        gameController.stepBackward()
                    }
                    isLoaded = true
                }
            }
            else -> {}
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

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
            scrollValue.animateScrollTo(scrollValue.viewportSize)
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
            ,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                BackButton {
                    navController.navigate(NAV_HOME)
                }

                if(isFromTrainer)
                if(name.isEmpty())
                    Icon(imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Dokoncit",
                        modifier = Modifier
                            .size(55.dp)
                            .padding(10.dp)
                            .clickable(onClick = {
                                    Firebase.auth.uid?.let {
                                        viewModelChessCourse.setChessTaskFinished(id,
                                            it
                                        )
                                    }
                            })
                        ,
                        tint = if(viewModelChessCourse.isFinished) Color.Green else Color.Red
                    )
                else
                    Icon(imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Dokoncit",
                        modifier = Modifier
                            .size(55.dp)
                            .padding(10.dp)
                            .clickable(onClick = {
                            })
                        ,
                        tint = if(isFinished.value) Color.Green else Color.Red
                    )


            }

            Column {

                BoardWithEnable(
                    gamePlayState = gamePlayState.value,
                    gameController = gameController,
                    isFlipped = isFlipped.value,
                    isBoardEnabled = false
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .background(colorResource(id = R.color.primary))
                        .horizontalScroll(scrollValue)
                ) {
                    Text(text = moves.value,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.primary),
                        contentColor = Color.Black
                    )

                ) {
                    Column(
                        Modifier
                            .height(150.dp)
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if(move.value != -1) {
                            if(name.isEmpty()) {
                                Text(
                                    chessGameTrainer.value.describeMove?.get(move.value) ?: " ",
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            } else {
                                Text(
                                    chessGame.value.describeMove?.get(move.value) ?: " ",
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            }

                        } else if(description.value != "") {
                            Text(
                                description.value,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            if(name.isEmpty()) {
                chessGameTrainer.value.moves?.let {
                    PlayingGameBottomMenu(
                        gameController,
                        chessGameTrainer.value.moves,
                        move = move,
                        isFlipped
                    )
                }
            } else {
                chessGame.value.moves?.let {
                    PlayingGameBottomMenu(
                        gameController,
                        chessGame.value.moves,
                        move = move,
                        isFlipped
                    )
                }
            }
        }
    }
}

@Composable
fun PlayingGameBottomMenu(gameController: GameController, moves: List<String>?, move: MutableIntState, isFlipped: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(vertical = 15.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(imageVector = Icons.Default.Refresh,
            contentDescription = "Restart",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    move.value = -1
                    for (movex in 0..<(moves?.size?.minus(1) ?: -1)) {
                        gameController.stepBackward()
                    }
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
                    if (moves != null)
                        if (move.value > 0)
                            move.value -= 1
                        else
                            move.value = -1

                })
        )

        Icon(imageVector = Icons.Default.ArrowForward,
            contentDescription = "Forward",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    gameController.stepForward()
                    if (moves != null) {
                        if (move.value < ((moves?.size?.minus(1)) ?: -1)) {
                            move.value += 1
                        }
                    }

                })
        )
    }
}