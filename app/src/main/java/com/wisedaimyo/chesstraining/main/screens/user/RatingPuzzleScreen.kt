package com.wisedaimyo.chesstraining.main.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.shimmerBrush
import com.wisedaimyo.chesstraining.main.data.models.NAV_HOME
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.UsersViewModel
import com.wisedaimyo.chesstraining.main.ui.theme.dimens

@Composable
fun RatingPuzzleScreen(
    viewmodel: UsersViewModel = hiltViewModel(),
    navController: NavController
){

    val usersList = remember { mutableStateOf(listOf<User>()) }
    viewmodel.getUsers()

    LaunchedEffect(key1 = viewmodel.userResponse) {
        when(val response = viewmodel.userResponse) {
            is Response.Success -> {
                response.data?.let {
                    usersList.value = it
                    usersList.value = it.sortedByDescending { user -> user.puzzleElo }
                }
            }
            else -> { }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {
        Column {
            BackButton {
                navController.navigate(NAV_HOME)
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                    Text(text = "Hádankové hodnotenie",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
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
                            "Najlepší hráči",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            "🐉",
                            fontSize = MaterialTheme.typography.displayLarge.fontSize,
                        )
                    }
                }
                items(usersList.value.size) { index ->
                    val player = usersList.value[index]
                    player.displayName?.let { player.puzzleElo?.let { it1 ->
                        RatingPlayerInfoPuzzle(it, player.isTrainer,
                            it1, getImageResId(context = LocalContext.current, player.image ?: "ic_launcher_foreground" ), player.photoUrl, player.puzzleSolved
                        )
                    } }
                }
            }
        }
    }
}


@Composable
fun RatingPlayerInfoPuzzle(name:String, isTrainer: Boolean? , elo: Int, image:Int? = null, photo: String? = null, solvedPuzzle: Int? = null) {

    val showShimmer = remember { mutableStateOf(true) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)

    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,) {

            if(photo.toString()=="null") {
                Image(
                    painter = painterResource(id = image ?: R.drawable.king_black),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(horizontal = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                )
            } else {
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
                        .size(50.dp)
                        .padding(horizontal = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.small1))

            Text(
                text = name + if (isTrainer == true) " 👨🏻‍🎓" else " 👨🏼‍🏫",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        Row {

            Text(
                text = "$elo",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Green
            )

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.small1))

            Text(
                text = "$solvedPuzzle",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.DarkGray
            )

        }
    }
}
