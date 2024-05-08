package com.wisedaimyo.chesstraining.main.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.CustomButton
import com.wisedaimyo.chesstraining.main.data.models.FINISH_REGISTER
import com.wisedaimyo.chesstraining.main.data.models.PLAY_ROBOT
import com.wisedaimyo.chesstraining.main.data.models.REGISTER_CHOOSE
import com.wisedaimyo.chesstraining.main.screens.Screen
import com.wisedaimyo.chesstraining.main.ui.theme.dimens
import kotlin.math.absoluteValue


@Composable
fun RegisterImageChooseScreen(
    isTrainer: Boolean = false,
    navigateToBack: () -> Unit,
    navController: NavController
) {
    val screenWidth = LocalConfiguration.current.screenHeightDp
    val images = arrayOf(
        R.drawable.dragon,
        R.drawable.dragonhead,
        R.drawable.lantern
        )

    val imagesChessPiecesWhite = arrayOf(
        R.drawable.king_white,
        R.drawable.queen_white,
        R.drawable.knight_white,
        R.drawable.bishop_white,
        R.drawable.rook_white
    )

    val imagesChessPiecesBlack = arrayOf(
        R.drawable.king_black,
        R.drawable.queen_black,
        R.drawable.knight_black,
        R.drawable.bishop_black,
        R.drawable.rook_black
    )


    var chosenImage by remember { mutableIntStateOf(R.drawable.img) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

        Column {

            BackButton {
                navigateToBack()
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {


                Text(
                    text = "Vyber si Avatar",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.padding(vertical = 15.dp))

                if (chosenImage == R.drawable.img) {
                    Image(
                        painter = painterResource(id = chosenImage),
                        contentDescription = "Profile Picture",
                    )
                } else {
                    Image(
                        painter = painterResource(id = chosenImage),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(((screenWidth / 3) / 2).dp)
                            .clip(RoundedCornerShape(1500.dp))
                            .background(Color.White)
                    )
                }

                Spacer(modifier = Modifier.padding(vertical = 15.dp))

                Text(
                    "Zoznam obrázkov",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.padding(vertical = 5.dp))

                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState())
                ) {

                    for (x in images) {
                        Image(
                            painter = painterResource(id = x),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(((screenWidth / 5) / 2).dp)
                                .padding(horizontal = 5.dp)
                                .clickable {
                                    chosenImage = x
                                }
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)

                        )
                    }
                }

                Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1))

                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState())
                ) {

                    for (x in imagesChessPiecesWhite) {
                        Image(
                            painter = painterResource(id = x),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(((screenWidth / 5) / 2).dp)
                                .padding(horizontal = 5.dp)
                                .clickable {
                                    chosenImage = x
                                }
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)

                        )
                    }
                }

                Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1))

                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState())
                ) {

                    for (x in imagesChessPiecesBlack) {
                        Image(
                            painter = painterResource(id = x),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(((screenWidth / 5) / 2).dp)
                                .padding(horizontal = 5.dp)
                                .clickable {
                                    chosenImage = x
                                }
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)

                        )
                    }
                }


                Spacer(modifier = Modifier.padding(vertical = 15.dp))

                CustomButton(name = "Ďalej")  {
                    navController.navigate("${Screen.FinishScreen.route}/${isTrainer}/$chosenImage")
                }
            }
        }
    }
}