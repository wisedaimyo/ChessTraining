package com.wisedaimyo.chesstraining.main.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.main.components.CustomButton
import com.wisedaimyo.chesstraining.main.components.Custom_Card
import com.wisedaimyo.chesstraining.main.components.shimmerBrush
import com.wisedaimyo.chesstraining.main.data.models.LEARN_GAME
import com.wisedaimyo.chesstraining.main.data.models.LEARN_MOVE_PIECES
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.ChessCourse
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.ChessCourseViewModel
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    viewModel: ChessCourseViewModel = hiltViewModel(),
    navController: NavController
) {
    var checkLearnToPlay by rememberSaveable { mutableStateOf(false) }
    var sheetState = rememberModalBottomSheetState()

    val listOpenings = remember { mutableStateOf<List<ChessCourse>>(listOf()) }
    val listMiddleGame = remember { mutableStateOf<List<ChessCourse>>(listOf()) }
    val listEndGame = remember { mutableStateOf<List<ChessCourse>>(listOf()) }

    LaunchedEffect(key1 = viewModel.getAllCoursesResponse) {
        when (viewModel.getAllCoursesResponse) {
            is Response.Success -> {
                val list = (viewModel.getAllCoursesResponse as Response.Success<List<ChessCourse>>).data!!
                val openings = mutableListOf<ChessCourse>()
                val middleGames = mutableListOf<ChessCourse>()
                val endGames = mutableListOf<ChessCourse>()
                for (chessGame in list) {
                    when (chessGame.type) {
                        "opening" -> openings.add(chessGame)
                        "middle" -> middleGames.add(chessGame)
                        "ending" -> endGames.add(chessGame)
                    }
                }
                listOpenings.value = openings
                listMiddleGame.value = middleGames
                listEndGame.value = endGames
            }
            else -> { }
        }

    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

        if(checkLearnToPlay) {
            ModalBottomSheet(
                onDismissRequest = { checkLearnToPlay = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val movePieces = listOf(
                            R.drawable.king_black,
                            R.drawable.queen_black,
                            R.drawable.rook_black,
                            R.drawable.knight_black,
                            R.drawable.bishop_black,
                            R.drawable.pawn_black
                        )

                        Text("Ako hýbať s figúrkami?")

                        MyGrid(data = movePieces, navController = navController) {
                            navController.navigate(LEARN_MOVE_PIECES)
                        }

//                        Text("Triky s pešiakom.")
//
//                        MyGrid(data = pawnTricks, navController = navController) {
//                           // TODO -> navController.navigate(MY_FEN_PUZZLE)
//                        }
//
//                        Text("Šach Mat")
//
//                        MyGrid(data = kingMate, navController = navController) {
//                            // TODO ->  navController.navigate(MY_FEN_PUZZLE)
//                        }
                    }
                }
            }
        }

        Column(
           modifier = Modifier.verticalScroll(rememberScrollState())

        ){
            Box(
                contentAlignment = Alignment.TopCenter
            ) {

                Image(
                    painter = painterResource(id = R.drawable.shape2),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Spoločný kurz",
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "fwa",
                        modifier = Modifier
                            .size(100.dp)
                        )
                }

            }

            val context = LocalContext.current

            Column{
                Column(modifier = Modifier.padding(10.dp)) {
                    CustomButton(name = "Nauč sa hrať") {
                        checkLearnToPlay = true
                    }
                }

                Text("Otvorenia",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 13.dp)
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {



                    for (game in listOpenings.value) {
                        game.name?.let {
                            Custom_Card(
                                false,
                                image =  getImageResId(context = context, game.img ?: "ic_launcher_foreground" ),
                                name = it
                            ) {
                                navController.navigate("$LEARN_GAME/${it}")
                            }
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.padding(vertical = 13.dp))

            Column {
                Text("Stredna hra",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 13.dp)
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {

                    if (listMiddleGame.value.isNotEmpty()) {
                        for (game in listMiddleGame.value) {
                            game.name?.let {
                                Custom_Card(
                                    false,
                                    image =  getImageResId(context = context, game.img ?: "ic_launcher_foreground" ),
                                    name = it
                                ) {
                                    navController.navigate("$LEARN_GAME/${it}")
                                }
                            }
                        }
                    } else {
                        ShimmeringBoxes()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 13.dp))

            Column {
                Text("Koncova hra",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 13.dp)
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    if (listEndGame.value.isNotEmpty()) {
                    for (game in listEndGame.value) {
                        game.name?.let {
                            Custom_Card( false,
                                image = getImageResId(context = context, game.img ?: "ic_launcher_foreground"  ),
                                name = it
                            ) {
                                navController.navigate("$LEARN_GAME/${it}")
                            }
                        }
                    }
                    } else {
                        ShimmeringBoxes()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(
                vertical = 30.dp
            ))

        }
    }
}

@Composable
fun MyGrid(data: List<Int>, navController: NavController, onClick: () -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(data.size) { item ->
            Card(
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .background(
                        Color(
                            red = Random.nextInt(0, 255),
                            green = Random.nextInt(0, 255),
                            blue = Random.nextInt(0, 255)
                        )
                    )
                    .padding(30.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .size(64.dp)
                ) {
                    Image(
                        painter = painterResource(id = data[item] ),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(52.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ShimmeringBoxes() {
    val showShimmer = remember { mutableStateOf(true) }

    Box(modifier = Modifier
        .padding(10.dp, 5.dp, 10.dp, 10.dp)
        .clip(RoundedCornerShape(30.dp))
        .background(
            shimmerBrush(
                targetValue = 1300f,
                showShimmer = showShimmer.value
            )
        )
        .height(150.dp)
        .width(150.dp))

    Box(modifier = Modifier
        .padding(10.dp, 5.dp, 10.dp, 10.dp)
        .clip(RoundedCornerShape(30.dp))
        .background(
            shimmerBrush(
                targetValue = 1300f,
                showShimmer = showShimmer.value
            )
        )
        .height(150.dp)
        .width(150.dp))

    Box(modifier = Modifier
        .padding(10.dp, 5.dp, 10.dp, 10.dp)
        .clip(RoundedCornerShape(30.dp))
        .background(
            shimmerBrush(
                targetValue = 1300f,
                showShimmer = showShimmer.value
            )
        )
        .height(150.dp)
        .width(150.dp))
}


@Preview
@Composable
fun CourseScreen_Preview() {
    CourseScreen(navController = rememberNavController())
}





