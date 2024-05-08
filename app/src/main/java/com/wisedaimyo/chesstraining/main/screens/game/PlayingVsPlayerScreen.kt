package com.wisedaimyo.chesstraining.main.screens.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.calculateEloRating
import com.wisedaimyo.chesstraining.calculateRemainingTime
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameProgress
import com.wisedaimyo.chesstraining.chess.ui.BoardWithEnable
import com.wisedaimyo.chesstraining.extractMove
import com.wisedaimyo.chesstraining.generateFEN
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.getPiece
import com.wisedaimyo.chesstraining.getPiecePromotion
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.shimmerBrush
import com.wisedaimyo.chesstraining.main.data.models.NAV_HOME
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.PlayedGame
import com.wisedaimyo.chesstraining.main.data.models.model.PlayingGame
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.PlayingVsPlayerViewModel
import com.wisedaimyo.chesstraining.stringToList
import com.wisedaimyo.chesstraining.whoPlays


@SuppressLint("SuspiciousIndentation")
@Composable
fun PlayingVsPlayerScreen(
    navController: NavController,
    viewModel: PlayingVsPlayerViewModel = hiltViewModel(),
    gameId: String = ""
    ) {
    viewModel.getGameWithId(gameId)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

        var isEnabledBoard = remember { mutableStateOf(true) }
        var isFlipped by remember { mutableStateOf(false) }

        var fromToMove = remember { mutableStateOf("") }

        var gamePlayState = remember { mutableStateOf( GamePlayState()) }

        var whoPlays by remember { mutableStateOf("whoPlays") }
        var currentPlayerColor by remember { mutableStateOf("currentPlayerColor") }

        val gameController = remember(gamePlayState.value) {
            GameController(
                getGamePlayState = { gamePlayState.value },
                setGamePlayState = { gamePlayState.value = it },
                preset = null
            )
        }

        LaunchedEffect(key1 = viewModel.chosenGame.value) {
            viewModel.getPlayersForGame(viewModel.chosenGame.value)

            whoPlays = viewModel.chosenGame.value.fen?.let { whoPlays(it) }.toString()
            if (viewModel.chosenGame.value.white == Firebase.auth.uid)
                currentPlayerColor = "White"
            else
                currentPlayerColor = "Black"

            isFlipped = currentPlayerColor != "White"

            if (viewModel.chosenGame.value.moves != null)
                for (moveToExtract in viewModel.chosenGame.value.moves!!) {
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
        }

        LaunchedEffect(key1 = viewModel.blackPlayer.value, key2 = viewModel.whitePlayer.value) {
            val time = viewModel.chosenGame.value.lastMove?.let { viewModel.chosenGame.value.time?.let { it1 ->
                calculateRemainingTime(it,
                    it1
                )
            } }
            if(time?.isEmpty() == true) gamePlayState.value.gameState.currentSnapshotState.gameProgress = GameProgress.LOSE_ON_TIME
        }

        LaunchedEffect(key1 = gamePlayState.value.gameState) {
            if(viewModel.chosenGame.value.moves!=null &&
                gamePlayState.value.gameState.toMove != if(currentPlayerColor == "White") SetPiece.WHITE else SetPiece.BLACK)
            if (gamePlayState.value.gameState.moves().size > viewModel.chosenGame.value.moves!!.size && fromToMove.value == ""){
                isEnabledBoard.value = false
                fromToMove.value = "${gamePlayState.value.gameState.moves().last().from}${gamePlayState.value.gameState.moves().last().to}"
            }
        }

        LaunchedEffect(key1 = viewModel.updateGameResponse) {
            if(viewModel.updateGameResponse == Response.Success(true)) {
                viewModel.chosenGame.value.moves
                navController.navigate(NAV_HOME)
            }
        }

        LaunchedEffect(key1 = gamePlayState.value.gameState.currentSnapshotState.gameProgress) {
            val moves = mutableListOf<String>()
            for(move in gamePlayState.value.gameState.states) {
                if(move.move != null) {
                    val move = "${move.move?.from}${move.move?.to}"
                    moves.add(move)
                }
            }

            var fenNotation = ""
            gamePlayState.value.gameState.currentSnapshotState.board.pieces.forEach { entry ->
                if (entry.value.setPiece.toString().equals("BLACK")) {
                    fenNotation += entry.key.name
                    fenNotation += entry.value.textSymbol.lowercase() + " "
                } else {
                    fenNotation += entry.key.name.uppercase()
                    fenNotation += entry.value.textSymbol.uppercase() + " "
                }
            }

            fenNotation = fenNotation.substring(0, fenNotation.length - 1)
            val list = stringToList(fenNotation)
            fenNotation = generateFEN(list, whiteMoves = currentPlayerColor != "White")

           if(gamePlayState.value.gameState.currentSnapshotState.gameProgress == GameProgress.CHECKMATE) {
               val playedGame =
                   PlayedGame(
                       black = viewModel.chosenGame.value.black,
                       blackElo = viewModel.blackPlayer.value.elo,
                       fen = fenNotation,
                       moves = moves.toList(),
                       lastMove = Timestamp.now(),
                       time = viewModel.chosenGame.value.time,
                       trainer = null,
                       white = viewModel.chosenGame.value.white,
                       whiteElo = viewModel.whitePlayer.value.elo,
                       winner = whoPlays,
                       wonBy = "CHECKMATE"
                    )

               viewModel.chosenGame.value.white?.let {
                   viewModel.chosenGame.value.black?.let { it1 ->
                       viewModel.addPlayedGame(
                           playedGame,
                           gameId,
                           whiteId = it,
                           eloWhite = calculateEloRating(
                               viewModel.whitePlayer.value.elo!!.toDouble(),
                               viewModel.blackPlayer.value.elo!!.toDouble(),
                               if(whoPlays == "White") 1.0 else -1.0
                           ).toInt(),
                           blackId = it1,
                           eloBlack = calculateEloRating(
                               viewModel.whitePlayer.value.elo!!.toDouble(),
                               viewModel.blackPlayer.value.elo!!.toDouble(),
                               if(whoPlays == "Black") 1.0 else -1.0
                           ).toInt()
                       )
                   }
               }
           }

            if(gamePlayState.value.gameState.currentSnapshotState.gameProgress == GameProgress.RESIGN) {
                val playedGame =
                    PlayedGame(
                        black = viewModel.chosenGame.value.black,
                        blackElo = viewModel.blackPlayer.value.elo,
                        fen = fenNotation,
                        moves = moves.toList(),
                        lastMove = Timestamp.now(),
                        time = viewModel.chosenGame.value.time,
                        trainer = null,
                        white = viewModel.chosenGame.value.white,
                        whiteElo = viewModel.whitePlayer.value.elo,
                        winner = if(whoPlays == "White") "Black" else "White",
                        wonBy = "CHECKMATE"
                    )

                viewModel.chosenGame.value.white?.let {
                    viewModel.chosenGame.value.black?.let { it1 ->
                        viewModel.addPlayedGame(
                            playedGame,
                            gameId,
                            whiteId = it,
                            eloWhite = calculateEloRating(
                                viewModel.whitePlayer.value.elo!!.toDouble(),
                                viewModel.blackPlayer.value.elo!!.toDouble(),
                                if(whoPlays == "White") -1.0 else 1.0
                            ).toInt(),
                            blackId = it1,
                            eloBlack = calculateEloRating(
                                viewModel.whitePlayer.value.elo!!.toDouble(),
                                viewModel.blackPlayer.value.elo!!.toDouble(),
                                if(whoPlays == "Black") -1.0 else 1.0
                            ).toInt()
                        )
                    }
                }
            }


           if(gamePlayState.value.gameState.currentSnapshotState.gameProgress == GameProgress.LOSE_ON_TIME) {
               val playedGame =
                   PlayedGame(
                       black = viewModel.chosenGame.value.black,
                       blackElo = viewModel.blackPlayer.value.elo,
                       fen = fenNotation,
                       moves = moves.toList(),
                       lastMove = Timestamp.now(),
                       time = viewModel.chosenGame.value.time,
                       trainer = null,
                       white = viewModel.chosenGame.value.white,
                       whiteElo = viewModel.whitePlayer.value.elo,
                       winner = if(whoPlays == "White") "Black" else "White",
                       wonBy = "LOSE_ON_TIME"
                   )

               viewModel.chosenGame.value.white?.let {
                   viewModel.chosenGame.value.black?.let { it1 ->
                       viewModel.addPlayedGame(
                           playedGame,
                           gameId,
                           whiteId = it,
                           eloWhite = calculateEloRating(
                               viewModel.whitePlayer.value.elo!!.toDouble(),
                               viewModel.blackPlayer.value.elo!!.toDouble(),
                               if(whoPlays == "White") -1.0 else 1.0
                           ).toInt(),
                           blackId = it1,
                           eloBlack = calculateEloRating(
                               viewModel.whitePlayer.value.elo!!.toDouble(),
                               viewModel.blackPlayer.value.elo!!.toDouble(),
                               if(whoPlays == "Black") -1.0 else 1.0
                           ).toInt()
                       )
                   }
               }
           }

           if(gamePlayState.value.gameState.currentSnapshotState.gameProgress == GameProgress.STALEMATE) {
               val playedGame =
                   PlayedGame(
                       black = viewModel.chosenGame.value.black,
                       blackElo = viewModel.blackPlayer.value.elo,
                       fen = fenNotation,
                       moves = moves.toList(),
                       lastMove = Timestamp.now(),
                       time = viewModel.chosenGame.value.time,
                       trainer = null,
                       white = viewModel.chosenGame.value.white,
                       whiteElo = viewModel.whitePlayer.value.elo,
                       winner = "Draw",
                       wonBy = "STALEMATE"
                   )



               viewModel.chosenGame.value.white?.let {
                   viewModel.chosenGame.value.black?.let { it1 ->
                       viewModel.addPlayedGame(
                           playedGame,
                           gameId,
                           whiteId = it,
                           eloWhite = calculateEloRating(
                               viewModel.chosenGame.value.white!!.toDouble(),
                               viewModel.chosenGame.value.black!!.toDouble(),
                               if(whoPlays == "White") 0.1 else -0.1
                           ).toInt(),
                           blackId = it1,
                           eloBlack = calculateEloRating(
                               viewModel.chosenGame.value.white!!.toDouble(),
                               viewModel.chosenGame.value.black!!.toDouble(),
                               if(whoPlays == "Black") 0.1 else -0.1
                           ).toInt()
                       )
                   }
               }
           }
        }

        LaunchedEffect(key1 = viewModel.removePlayingGame, key2 = viewModel.updateEloBlack, key3 = viewModel.updateEloWhite) {
            if(
                viewModel.updateEloWhite == Response.Success(true)
                &&
                viewModel.updateEloBlack == Response.Success(true)
                &&
                viewModel.removePlayingGame == Response.Success(true)) {
                navController.navigate(NAV_HOME)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {

                BackButton {
                    navController.navigate(NAV_HOME)
                }

                Column {
                    if (fromToMove.value.isNotEmpty())
                    Button(onClick = {
                        val moves = mutableListOf<String>()
                        for(move in gamePlayState.value.gameState.states) {
                            println("MOVES ${move.lastMove?.piece}")
                            if(move.move != null) {
                                val move = "${move.move?.from}${move.move?.to}"
                                moves.add(move)
                            }
                        }

                        var fenNotation = ""
                        gamePlayState.value.gameState.currentSnapshotState.board.pieces.forEach { entry ->
                            if (entry.value.setPiece.toString().equals("BLACK")) {
                                fenNotation += entry.key.name
                                fenNotation += entry.value.textSymbol.lowercase() + " "
                            } else {
                                fenNotation += entry.key.name.uppercase()
                                fenNotation += entry.value.textSymbol.uppercase() + " "
                            }
                        }

                        fenNotation = fenNotation.substring(0, fenNotation.length - 1)
                        val list = stringToList(fenNotation)
                        fenNotation = generateFEN(list, whiteMoves = currentPlayerColor != "White")

                        var newMoves = viewModel.chosenGame.value.moves?.toMutableList()
                        if (newMoves != null) {
                            newMoves.add("${moves[moves.size-1]}${getPiecePromotion(gamePlayState.value.promotionState.toString(), whoPlays == "White")}")
                        }

                         val newGame =  PlayingGame(
                               black = viewModel.chosenGame.value.black,
                               white = viewModel.chosenGame.value.white,
                               fen = fenNotation,
                               time = viewModel.chosenGame.value.time,
                               lastMove = Timestamp.now(),
                               moves = newMoves
                           )

                        viewModel.updateGame(gameId, newGame)

                    }, modifier = Modifier.padding(horizontal = 10.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "",
                                modifier = Modifier.size(30.dp)
                            )

                            Spacer(modifier = Modifier.padding(horizontal = 2.dp))

                            Text(text = "Odoslať")

                        }
                    }
                }

            }


            if(viewModel.whitePlayer.value.displayName != viewModel.currentUser.value.displayName)
                viewModel.whitePlayer.value.displayName?.let {
                    viewModel.whitePlayer.value.elo?.let { it1 ->
                        viewModel.chosenGame.value.lastMove?.let { it2 ->
                            viewModel.chosenGame.value.time?.let { it3 ->
                                calculateRemainingTime(
                                    Timestamp.now(),
                                    it3
                                )
                            }?.let { it4 ->
                                PlayerStringComposable(
                                    player = it,
                                    elo = "$it1",
                                    isOnline = true,
                                    time = it4,
                                    image =  getImageResId(context = LocalContext.current, viewModel.whitePlayer.value.image ?: "ic_launcher_foreground" ),
                                    photo = viewModel.whitePlayer.value.photoUrl
                                )
                            }

                        }
                    }
                }
            else
                viewModel.blackPlayer.value.displayName?.let {
                    viewModel.blackPlayer.value.elo?.let { it1 ->
                        viewModel.chosenGame.value.lastMove?.let { it2 ->
                            viewModel.chosenGame.value.time?.let { it3 ->
                                calculateRemainingTime(
                                    Timestamp.now(),
                                    it3
                                )
                            }?.let { it4 ->
                                PlayerStringComposable(
                                    player = it,
                                    elo = "$it1",
                                    isOnline = true,
                                    time = it4,
                                    image = getImageResId(context = LocalContext.current, viewModel.blackPlayer.value.image ?: "ic_launcher_foreground" ),
                                    photo = viewModel.blackPlayer.value.photoUrl
                                )
                            }
                            }
                    }
                }

            BoardWithEnable(
                gamePlayState = gamePlayState.value,
                gameController = gameController,
                isFlipped = isFlipped,
                isBoardEnabled = (whoPlays == currentPlayerColor) && isEnabledBoard.value
            )

            if(viewModel.whitePlayer.value.displayName == viewModel.currentUser.value.displayName)
                viewModel.whitePlayer.value.displayName?.let {
                    viewModel.whitePlayer.value.elo?.let { it1 ->
                        viewModel.chosenGame.value.lastMove?.let { it2 ->
                            viewModel.chosenGame.value.time?.let { it3 ->
                                calculateRemainingTime(
                                    timestamp = it2,
                                    hoursToAdd = it3
                                )
                            }
                        }?.let { it4 ->
                            PlayerStringComposable(
                                player = it,
                                elo = "$it1",
                                isOnline = true,
                                time = "${it4}",
                                image =  getImageResId(context = LocalContext.current, viewModel.whitePlayer.value.image ?: "ic_launcher_foreground" ),
                                photo = viewModel.whitePlayer.value.photoUrl
                            )
                        }
                    }
                }
            else
                viewModel.blackPlayer.value.displayName?.let {
                    viewModel.blackPlayer.value.elo?.let { it1 ->
                        viewModel.chosenGame.value.lastMove?.let { it2 ->
                            viewModel.chosenGame.value.time?.let { it3 ->
                                calculateRemainingTime(
                                    it2,
                                    it3
                                )
                            }
                        }?.let { it3 ->
                            PlayerStringComposable(
                                player = it,
                                elo = "$it1",
                                isOnline = true,
                                time = it3,
                                image = getImageResId(context = LocalContext.current, viewModel.blackPlayer.value.image ?: "ic_launcher_foreground" ),
                                photo = viewModel.blackPlayer.value.photoUrl
                            )
                        }
                    }
                }


            val moves = rememberSaveable { mutableStateOf("") }

            LaunchedEffect(key1 = gamePlayState.value.gameState.states) {
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

            PlayingVsScreenMenu(gamePlayState, gameController, moves, fromToMove, isEnabledBoard)
        }
    }
}

@Composable
fun PlayingVsScreenMenu(
    gamePlayState: MutableState<GamePlayState>,
    gameController: GameController, moves: MutableState<String>, fromToMove: MutableState<String>, isEnabledBoard: MutableState<Boolean>) {
    val scrollValue = rememberScrollState()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(colorResource(id = R.color.primary))
                .horizontalScroll(scrollValue)
        ) {
            Text(text = moves.value,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .clickable(onClick = {
                        if(fromToMove.value.isNotEmpty()) {
                            fromToMove.value = ""
                            gameController.stepBackward()
                            isEnabledBoard.value = true
                        }
                    })
            ) {
                Icon(imageVector = Icons.Default.Refresh,
                    contentDescription = "",
                    Modifier.size(50.dp))
                Text(text = "Obnov")
            }



            Column {
                IconButton(onClick = {
                    gamePlayState.value.gameState.currentSnapshotState.gameProgress = GameProgress.RESIGN
                }) {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier.size(80.dp))
                }
                Text(text = "Vzdať sa")
            }


        }
    }
}

@Composable
fun PlayerStringComposable(
    player: String,
    elo: String,
    isOnline: Boolean,
    time: String,
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
                        .height(50.dp)
                        .width(150.dp)
                        .shadow(30.dp)
                    ,

                    shape = RoundedCornerShape(20.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(time,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = 25.dp, vertical = 15.dp)
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                        ,
                        maxLines = 1
                    )
                }
            }
        }
    }
}