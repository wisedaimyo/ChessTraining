package com.wisedaimyo.chesstraining.main.screens.game

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.calculateEloRating
import com.wisedaimyo.chesstraining.chess.model.board.Position.*
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece.*
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.ui.Board
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.data.models.NAV_TRAINING
import com.wisedaimyo.chesstraining.chess.model.board.Board
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GameProgress
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.main.components.shimmerBrush
import com.wisedaimyo.chesstraining.main.data.models.PLAY_PUZZLE
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.UsersViewModel
import com.wisedaimyo.chesstraining.main.screens.main.dataStore
import com.wisedaimyo.chesstraining.main.ui.theme.dimens
import com.wisedaimyo.chesstraining.parseFEN
import com.wisedaimyo.chesstraining.stringToList
import com.wisedaimyo.chesstraining.whoPlays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")


@Composable
fun PlayingPuzzleScreen(
    viewmodel: UsersViewModel = hiltViewModel(),
    elo: Int = 1500,
    navController: NavController
){
    var isFlipped by rememberSaveable { mutableStateOf(false) }
    val gamePlayState = rememberSaveable { mutableStateOf( GamePlayState()) }
    val isFinished = rememberSaveable { mutableStateOf( false) }
    var move = rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current
    val puzzles = remember { ChessPuzzleCsvParser.parsePuzzlesFromCsv(context, elo-150, elo+150) }
    var winPlayer: MediaPlayer? = null
    var losePlayer: MediaPlayer? = null
    val MUSIC_KEY = booleanPreferencesKey("music")
    val getMusic: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[MUSIC_KEY]
    }
    val isMusic by getMusic.collectAsState(initial = false)


    winPlayer = MediaPlayer().apply {
        val afd = context.resources.openRawResourceFd(R.raw.win)
        if (afd == null) {
        } else {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            prepare()
        }
    }

    losePlayer = MediaPlayer().apply {
        val afd = context.resources.openRawResourceFd(R.raw.lose)
        if (afd == null) {
        } else {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            prepare()
        }
    }

    val gameController = remember {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it }
        ).apply {
            if (puzzles != null) {
                val board = Board(
                    pieces = parseFEN(puzzles.FEN)
                )
                reset(
                    GameSnapshotState(
                        board = board,
                        toMove = if(whoPlays(puzzles.FEN) == "White") WHITE else BLACK
                    )
                ) }
        }

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ){

            LaunchedEffect(key1 = puzzles) {
                if (puzzles != null) {
                    isFlipped = whoPlays(puzzles.FEN) == "White"
                }
            }

            LaunchedEffect(key1 = gameController.toMove.name) {
                if (puzzles != null && !isFinished.value) {
                    if ((move.value % 2) == 0) {
                        val moves = stringToList(puzzles.Moves)
                        if (move.value != 0) {
                            val fromMove =
                                "${gamePlayState.value.gameState.currentSnapshotState.lastMove?.from?.toString()}"
                            val toMove =
                                "${gamePlayState.value.gameState.currentSnapshotState.lastMove?.to?.toString()}"
                            val checkMove = "$fromMove$toMove"

                            if (checkMove == moves[move.value - 1]) {
                                gameController.apply {
                                    val from =
                                        Position.fromString(moves[move.value].substring(0, 2))
                                    val to = Position.fromString(moves[move.value].substring(2, 4))
                                    applyMove(from, to)
                                }
                            } else {
                                gamePlayState.value.gameState.currentSnapshotState.gameProgress =
                                    GameProgress.LOSE_PUZZLE

                                val user = viewmodel.currentUser
                                user.puzzleElo = user.puzzleElo?.let { calculateEloRating(it.toDouble(), puzzles.Rating.toDouble(), -1.0).toInt() }
                                user.puzzleStrike = 0

                                Firebase.auth.currentUser?.let { viewmodel.addUserToFirestore(user = user, it.uid) }

                                if(isMusic != true)
                                losePlayer.start()

                                Toast.makeText(context, "ZLE!!", Toast.LENGTH_LONG).show()
                                isFinished.value = true
                            }
                        } else {
                            gameController.apply {
                                val from = Position.fromString(moves[move.value].substring(0, 2))
                                val to = Position.fromString(moves[move.value].substring(2, 4))
                                applyMove(from, to)
                            }
                        }
                    }


                    if (move.value < stringToList(puzzles.Moves).size - 1) {
                        move.value++
                    } else {
                        Toast.makeText(context, "DOBRE!!", Toast.LENGTH_LONG).show()
                        if(isMusic != true)
                        winPlayer.start()

                        val user = viewmodel.currentUser
                        user.puzzleSolved = user.puzzleSolved?.plus(1) ?: 1
                        user.puzzleElo = user.puzzleElo?.let { calculateEloRating(it.toDouble(), puzzles.Rating.toDouble(), 1.0).toInt() }
                        user.puzzleStrike = user.puzzleStrike?.plus(1) ?: 0

                        Firebase.auth.currentUser?.let { viewmodel.addUserToFirestore(user = user, it.uid) }

                        gamePlayState.value.gameState.currentSnapshotState.gameProgress =
                            GameProgress.WIN_PUZZLE
                        isFinished.value = true
                    }
                }
            }

            BackButton {
                navController.navigate(NAV_TRAINING)
            }

            Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {

                if (puzzles != null) {
                    Text(text = if(whoPlays(puzzles.FEN) != "White") "Biely na ťahu" else "Čierny na ťahu",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        color = Color.White,
                    )

                Text(text = "🏆",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize)

                    Text(text = puzzles.Rating,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1))

            Board(
                gamePlayState = gamePlayState.value,
                gameController = gameController,
                isFlipped = isFlipped
            )


            PlayerComposable(
                player = viewmodel.currentUser.displayName ?: "",
                elo = "${viewmodel.currentUser.puzzleElo}",
                image = getImageResId(context = LocalContext.current, viewmodel.currentUser.image ?: "ic_launcher_foreground" ),
                playerPhoto = viewmodel.currentUser.photoUrl,
                playerStrike = viewmodel.currentUser.puzzleStrike
                )

            Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                if(!isFinished.value) {
                    Column(
                        modifier = Modifier.clickable(onClick = {
                            val user = viewmodel.currentUser
                            user.puzzleStrike =  0
                            Firebase.auth.currentUser?.let { viewmodel.addUserToFirestore(user = user, it.uid) }

                            navController.navigate("$PLAY_PUZZLE/${viewmodel.currentUser.elo}")
                        }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Next Puzzle",
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text("Ďalšie")
                    }

                    Column(
                        modifier = Modifier.clickable(onClick = {
                            if (puzzles != null) {
                                val user = viewmodel.currentUser
                                user.puzzleElo = user.puzzleElo?.let { calculateEloRating(it.toDouble(), puzzles.Rating.toDouble(), -0.2).toInt() }
                                Firebase.auth.currentUser?.let { viewmodel.addUserToFirestore(user = user, it.uid) }

                                Toast.makeText(context, "  ${stringToList(puzzles.Moves)[move.value-1]} \n Stratil si ELO", Toast.LENGTH_LONG).show()
                            }
                        }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Next Puzzle",
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text("Pomoc")
                    }

                } else {
                    Column(
                        modifier = Modifier.clickable(onClick = {
                            gameController.stepBackward()
                        }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text("Dozadu")
                    }

                    Column(
                        modifier = Modifier.clickable(onClick = {
                            navController.navigate("$PLAY_PUZZLE/${viewmodel.currentUser.elo}")
                        }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Next Puzzle",
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text("Ďalšie")
                    }

                    Column(
                        modifier = Modifier.clickable(onClick = {
                            gameController.stepForward()
                        }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Puzzle",
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text("Dopredu")
                    }

                }

            }



        }
    }
}

@Composable
fun PlayerComposable(player: String, elo: String, image: Int?, playerPhoto: String?, playerStrike: Int?) {
    val screenWidth = LocalConfiguration.current.screenHeightDp
    val showShimmer = remember { mutableStateOf(true) }
    val imageSize = ((screenWidth / 6) / 2).dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp)
    ) {

        if (image!=null) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "avatar",
                modifier = Modifier
                    .size(imageSize)
            )
        } else if (playerPhoto!=null) {
            AsyncImage(
                model = playerPhoto.toString(),
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
        }

        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column{
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

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

                Text(text = "⭐️ $playerStrike",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = Color.White
                    )
            }
        }
    }
}