package com.wisedaimyo.chesstraining.main.screens.user

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Board
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.ui.BoardWithEnable
import com.wisedaimyo.chesstraining.formatTimestampIntoReadableFormat
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.shimmerBrush
import com.wisedaimyo.chesstraining.main.data.models.ANALYZE_GAME
import com.wisedaimyo.chesstraining.main.data.models.NAV_HOME
import com.wisedaimyo.chesstraining.main.data.models.model.PlayedGame
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.PlayedGamesViewModel
import com.wisedaimyo.chesstraining.main.ui.theme.dimens
import com.wisedaimyo.chesstraining.parseFEN
import com.wisedaimyo.chesstraining.whoPlays


@SuppressLint("SuspiciousIndentation")
@Composable
fun PlayedGamesScreen(
    viewModel: PlayedGamesViewModel = hiltViewModel(),
    navController: NavController
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

        Column(
            Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                BackButton {
                    navController.navigate(NAV_HOME)
                }

                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Pridaj Partiu",
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(35.dp)
                        .clickable(onClick = {
                            navController.navigate(ANALYZE_GAME)
                        })
                )

            }

            if(viewModel.gamesList.isNotEmpty()) {
                if(viewModel.gamesList.size>0)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(vertical = 25.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Odohraté partie",
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Text(
                                "♛",
                                fontSize = MaterialTheme.typography.displayLarge.fontSize,
                            )
                        }
                    }
                    items(viewModel.gamesList.size) { index ->
                        val game = viewModel.gamesList[index]
                        game.second.fen?.let {
                            ChessInfoPlayed(
                                fen = it,
                                playedGame = game.second,
                                onClick = {
                                    navController.navigate("$ANALYZE_GAME/${game.first}")
                                })
                        }
                        Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1))
                    }
                }
            } else {
                ShimmeringViewsPlayedGames()
            }
        }
    }
}

@Composable
fun ShimmeringViewsPlayedGames() {
    val showShimmer = remember { mutableStateOf(true) }
    val screenWidth = LocalConfiguration.current.screenHeightDp
    val imageSize = (screenWidth/3).dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .padding(top = 120.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Asi nemáš odohraté partie",
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(
                    shimmerBrush(
                        targetValue = 1300f,
                        showShimmer = showShimmer.value
                    )
                )
                .width(screenWidth.dp)
                .heightIn(min = imageSize)
                .padding(vertical = 30.dp)
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(
                    shimmerBrush(
                        targetValue = 1300f,
                        showShimmer = showShimmer.value
                    )
                )
                .width(screenWidth.dp)
                .heightIn(min = imageSize)
                .padding(vertical = 30.dp)

        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(
                    shimmerBrush(
                        targetValue = 1300f,
                        showShimmer = showShimmer.value
                    )
                )
                .width(screenWidth.dp)
                .heightIn(min = imageSize)
                .padding(vertical = 30.dp)
        )


    }
}

@Composable
fun ChessInfoPlayed(fen: String, playedGame: PlayedGame, onClick: () -> Unit) {
    val gamePlayState = rememberSaveable { mutableStateOf( GamePlayState()) }
    val currentPlayerPlayedAs =
        if(Firebase.auth.uid == playedGame.white) "White" else "Black"


    val gameController = remember {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it }
        ).apply {
            if (playedGame != null) {
                val board = Board(
                    pieces = parseFEN(fen = fen)
                )
                reset(
                    GameSnapshotState(
                        board = board,
                        toMove = if(whoPlays(fen) == "White") SetPiece.WHITE else SetPiece.BLACK
                    )
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.primary))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .size(150.dp)
        ) {

            BoardWithEnable(
                gamePlayState = gamePlayState.value,
                gameController = gameController,
                isFlipped = currentPlayerPlayedAs == "Black",
                isBoardEnabled = false
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Text(text =
            if(playedGame.wonBy == "CHECKMATE")
                "1 - 0 Šach Mat - ${(playedGame.time ?: 0) / 24} dni"
            else if(playedGame.wonBy == "RESIGN")
                "1 - 0 Vzdal sa ${(playedGame.time ?: 0)/ 24} dni"
            else if(playedGame.wonBy == "LOSE_ON_TIME")
                "1 - 0 prehra na čas ${(playedGame.time ?: 0)/ 24} dni"
            else if(playedGame.wonBy == "STALEMATE")
                "1/2 - 1/2 PAT ${(playedGame.time ?: 0)/ 24} dni"
            else
                "1/2 - 1/2 Remíza ${(playedGame.time ?: 0)/ 24} dni",

                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(text = "${playedGame.lastMove?.let { formatTimestampIntoReadableFormat(it.seconds)}}",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1))

            Column(
                modifier = Modifier
                 .background(colorResource(id = R.color.text)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    PlayerComposable(name = "Biely",
                        elo = playedGame.whiteElo ?: 0,
                       color = if(playedGame.winner == "White") Color.Green
                               else if (playedGame.winner == "Black") Color.Red
                               else Color.Yellow
                    )

                    Spacer(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.small1))

                    Text(
                        "VS",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.small1))

                    PlayerComposable(
                        name = "Čierny",
                        elo = playedGame.blackElo ?: 0,
                        color = if(playedGame.winner == "White") Color.Red
                                else if (playedGame.winner == "Black") Color.Green
                                else Color.Yellow)
                }

                Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if(currentPlayerPlayedAs == playedGame.winner)
                            "Vyhral si"
                        else
                            "Prehral si",

                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = if(currentPlayerPlayedAs == playedGame.winner) Color.Green else Color.Red
                    )
                }

                Spacer(modifier = Modifier.padding(vertical = 2.dp))
            }

        }
    }
}

@Preview
@Composable
fun PlayedGamesScreen_Preview() {
    PlayedGamesScreen(navController = rememberNavController())
}

@Composable
fun PlayerComposable(name: String, elo: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
            Text(
                name,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                "$elo",
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
    }
}