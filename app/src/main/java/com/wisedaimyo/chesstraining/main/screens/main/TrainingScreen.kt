package com.wisedaimyo.chesstraining.main.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.components.CustomButton
import com.wisedaimyo.chesstraining.main.data.models.PLAY_PUZZLE
import com.wisedaimyo.chesstraining.main.data.models.PLAY_ROBOT
import com.wisedaimyo.chesstraining.main.data.models.SHOW_PUZZLE_RATING
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.UsersViewModel

@Composable
fun TrainingScreen(
    viewmodel: UsersViewModel = hiltViewModel(),
    navController: NavController) {

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenHeightDp

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

        Column(
            Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
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
                        stringResource(R.string.your_training),
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )

                    Icon(imageVector = Icons.Default.ThumbUp,
                        contentDescription = "fwa")

                    Spacer(modifier = Modifier.padding(vertical = 13.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.solved_puzzle_num),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = Color.White
                        )
                        Text("${viewmodel.currentUser.puzzleSolved}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            color = Color.LightGray

                            )
                    }


                }

            }

            Spacer(modifier = Modifier.padding(vertical = 13.dp))

            Text(
                stringResource(R.string.puzzle),
                fontWeight = FontWeight.ExtraBold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                color = Color.White
                )

           Column(
               modifier = Modifier
                   .padding(top = 40.dp)
                   .height((screenHeight / 2.5).dp)
                   .width((screenWidth / 2.5).dp)
                   .clip(shape = RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp))
                   .background(colorResource(id = R.color.primary))
               //.shadow(elevation = 3.dp, shape = RoundedCornerShape(30.dp))
           ) {
               Column(
                   modifier = Modifier.padding(13.dp),
                   horizontalAlignment = Alignment.CenterHorizontally
               ) {

                   Text("Rieš šachové hádanky",
                       fontWeight = FontWeight.ExtraBold,
                       fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                       color = Color.White
                       )

                   CustomButton(name = stringResource(R.string.solve_puzzle), buttonColor = R.color.button_other,
                       onClick = {
                           navController.navigate("$PLAY_PUZZLE/${viewmodel.currentUser.puzzleElo}") {
                               popUpTo(PLAY_PUZZLE) { inclusive = true }
                               launchSingleTop = true
                           }
                       })
                   CustomButton(name = stringResource(R.string.leaderboard),
                       onClick = {
                           navController.navigate(SHOW_PUZZLE_RATING)
                       })

                   Spacer(modifier = Modifier.padding(vertical = 13.dp))

                    Text(text = "🔥",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.displayLarge.fontSize,
                        color = Color.Black
                        )

               }
           }
        }
    }
}
