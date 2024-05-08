package com.wisedaimyo.chesstraining.main.screens.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.wisedaimyo.chesstraining.API.RetrofitInstance
import com.wisedaimyo.chesstraining.API.data.NextMoveImpl
import com.wisedaimyo.chesstraining.generateFEN
import com.wisedaimyo.chesstraining.API.presentation.NextMoveViewModel
import com.wisedaimyo.chesstraining.stringToList
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.canCastle
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz.ActiveDatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.Queen
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameProgress
import com.wisedaimyo.chesstraining.chess.model.state.PromotionState
import com.wisedaimyo.chesstraining.chess.ui.BoardWithEnable
import com.wisedaimyo.chesstraining.chess.ui.pieces.Pieces
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.getPiece
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.ChessEvaluationBar
import com.wisedaimyo.chesstraining.main.components.shimmerBrush
import com.wisedaimyo.chesstraining.main.data.models.NAV_HOME
import com.wisedaimyo.chesstraining.main.data.models.PLAY_ROBOT
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.UsersViewModel
import com.wisedaimyo.chesstraining.main.ui.theme.dimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.random.Random

@SuppressLint("SuspiciousIndentation")
@Composable
fun PlayingVsScreen(
    viewmodel: UsersViewModel = hiltViewModel(),
    navController: NavController,
    time: Long,
    difficulty: Int) {
    val isFlipped by rememberSaveable { mutableStateOf(Random.nextBoolean()) }

    var isEnabledBoard by rememberSaveable { mutableStateOf(false) }

    var fenNotation by rememberSaveable { mutableStateOf("") }
    var gamePlayState = rememberSaveable { mutableStateOf( GamePlayState()) }

    var whitePlayerTimer by remember { mutableStateOf(time) }
    var timerRunningWhite by remember { mutableStateOf(true) }
    var blackPlayerTimer by remember { mutableStateOf(time) }
    var timerRunningBlack by remember { mutableStateOf(false) }

    val aiDifficulty = arrayOf("Lahky", "Stedny", "Tazky", "Majster", "Genius")
    val aiDifficultyLevel = arrayOf(1, 5, 8, 11, 13)
    val aiDifficultyELO = arrayOf("1000", "1500", "2000", "2500", "2700")

    val viewModel = viewModel { NextMoveViewModel(NextMoveImpl(RetrofitInstance.api)) }

    val gameController = remember(gamePlayState.value) {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it },
            preset = null
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {

            LaunchedEffect(key1 = whitePlayerTimer, key2 = timerRunningWhite) {
                if (gamePlayState.value.gameState.currentSnapshotState.gameProgress == GameProgress.IN_PROGRESS)
                while (whitePlayerTimer > 0 && timerRunningWhite) {
                    delay(1000L)
                    whitePlayerTimer--
                    if(gamePlayState.value.gameState.currentSnapshotState.toMove == SetPiece.BLACK) {
                        timerRunningWhite = false
                        timerRunningBlack = true
                    }
                }

                if(isFlipped) { isEnabledBoard = true } else { isEnabledBoard = false }
                if(whitePlayerTimer.toInt() == 0) {
                    gamePlayState.value.gameState.currentSnapshotState.gameProgress = GameProgress.LOSE_ON_TIME

                }

            }

            LaunchedEffect(key1 = blackPlayerTimer, key2 = timerRunningBlack) {
                if (gamePlayState.value.gameState.currentSnapshotState.gameProgress == GameProgress.IN_PROGRESS)
                while (blackPlayerTimer > 0 && timerRunningBlack) {
                    delay(1000L)
                    blackPlayerTimer--
                    if(gamePlayState.value.gameState.currentSnapshotState.toMove == SetPiece.WHITE) {
                        timerRunningWhite = true
                        timerRunningBlack = false
                    }
                }
                if(!isFlipped) { isEnabledBoard = true } else { isEnabledBoard = false }

            }

            LaunchedEffect(key1 = gameController, key2 = blackPlayerTimer) {
                fenNotation = ""
                gamePlayState.value.gameState.currentSnapshotState.board.pieces.forEach { entry ->
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

                if(!isFlipped) {
                    if (gamePlayState.value.gameState.currentSnapshotState.toMove == SetPiece.BLACK) {
                        viewModel.getNextMove(fenNotation, aiDifficultyLevel[difficulty], "bestmove")
                    }
                } else {
                    if (gamePlayState.value.gameState.currentSnapshotState.toMove == SetPiece.WHITE) {
                        viewModel.getNextMove(fenNotation, aiDifficultyLevel[difficulty], "bestmove")
                    }
                }

                viewModel.getEvaluation(fenNotation)
            }

            LaunchedEffect(key1 = gameController, key2 = whitePlayerTimer) {
                fenNotation = ""
                gamePlayState.value.gameState.currentSnapshotState.board.pieces.forEach { entry ->
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

                if(!isFlipped) {
                    if (gamePlayState.value.gameState.currentSnapshotState.toMove == SetPiece.BLACK) {
                        viewModel.getNextMove(fenNotation, aiDifficultyLevel[difficulty], "bestmove")
                    }
                } else {
                    if (gamePlayState.value.gameState.currentSnapshotState.toMove == SetPiece.WHITE) {
                        viewModel.getNextMove(fenNotation, aiDifficultyLevel[difficulty], "bestmove")
                    }
                }

                viewModel.getEvaluation(fenNotation)
            }

            LaunchedEffect(key1 = viewModel.promotion) {
                viewModel.promotion.collectLatest { move ->
                    if(!move.first.equals("") || !move.second.equals("") || !move.third.equals("")) {
                        val fromPosition = Position.fromString(viewModel.promotion.value.first ?: "")
                        val toPosition = Position.fromString(viewModel.promotion.value.second ?: "")
                        val piece = getPiece(move.third[0])

                        gameController.apply {
                            gameController.apply {
                                applyMove(fromPosition, toPosition)
                                onPromotionPieceSelected(piece)
                                applyMove(fromPosition, toPosition)
                            }
                        }
                        gamePlayState.value.gameState.currentSnapshotState.toMove.change()

                    }
                }
            }

            LaunchedEffect(key1 = viewModel.fromTo) {
                viewModel.fromTo.collectLatest { move ->
                    if(!move.first.equals("") || !move.second.equals("")) {
                        val fromPosition = Position.fromString(move?.first ?: "")
                        val toPosition = Position.fromString(move?.second ?: "")
                        gameController.applyMove(fromPosition, toPosition)
                    }
                }
            }

            BackButton(onClick = {
                navController.navigate(NAV_HOME)
            })

            PlayerComposable(aiDifficulty[difficulty],
                aiDifficultyELO[difficulty],true,
                if (!isFlipped) blackPlayerTimer else whitePlayerTimer,
                null,
                null
                )


                BoardWithEnable(
                    gamePlayState = gamePlayState.value,
                    gameController = gameController,
                    isFlipped = isFlipped,
                    isBoardEnabled = isEnabledBoard
                )

            PlayerComposable(viewmodel.currentUser.displayName ?: "",
                "${viewmodel.currentUser.elo ?: "0" }" ,
                true,
                if (!isFlipped) whitePlayerTimer else blackPlayerTimer,
                getImageResId(context = LocalContext.current, viewmodel.currentUser.image ?: "ic_launcher_foreground" ),
                viewmodel.currentUser.photoUrl)

            BoardControllerComposable()

            ChessEvaluationBar(evaluation = viewModel.evaluation, leadingSide = if(isFlipped) "black" else "white")

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.buttonHeight))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    navController.navigate("$PLAY_ROBOT/${time}/${difficulty}") {
                        popUpTo(PLAY_ROBOT) { inclusive = true }
                    }
                }) {
                    Text(text = "Hraj odznova")
                }
            }
        }
    }
}

@Composable
fun BoardControllerComposable() {

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

    }
}


@Composable
fun PlayerComposable(
    player: String,
    elo: String,
    isOnline: Boolean,
    time: Long,
    image: Int?,
    photo: String?
                     ) {
    val screenWidth = LocalConfiguration.current.screenHeightDp
    val showShimmer = remember { mutableStateOf(true) }
    val imageSize = ((screenWidth / 6) / 2).dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp)
    ) {

        if(image==null && photo==null) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "avatar",
                modifier = Modifier
                    .size(imageSize)
            )
        }

        if(photo!=null) {
            AsyncImage(
                model = photo.toString(),
                contentDescription = "Profile Picture",
                onSuccess = { showShimmer.value = false },
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = showShimmer.value
                        )
                    )
                    .width(imageSize)
                    .heightIn(min = imageSize)
                    .size(imageSize)
            )
        } else {
            image?.let { painterResource(id = it) }?.let {
                Image(
                    painter = it,
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(((screenWidth / 6) / 2).dp)
                )
            }
        }


        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Canvas(modifier = Modifier.size(10.dp), onDraw = {
                            val size = 10.dp.toPx()
                            drawCircle(
                                color = if (isOnline) Color.Green else Color.Red,
                                radius = size / 2f
                            )
                            drawCircle(
                                color = Color.Black,
                                radius = size / 2f,
                                style = Stroke(width = 0.5.dp.toPx())
                            )
                        })

                        Spacer(modifier = Modifier.padding(horizontal = 3.dp))

                        Column {

                            Text(
                                player,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                color = Color.White,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )

                        }
                    }

                    Text(elo,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

                Card(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .shadow(30.dp)
                        .height(50.dp),

                    shape = RoundedCornerShape(20.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("${time/60}m ${time%60}s",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = 25.dp, vertical = 15.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}