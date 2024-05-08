package com.wisedaimyo.chesstraining.main.screens.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.calculateRemainingTime
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.main.components.CustomButton
import com.wisedaimyo.chesstraining.main.components.CustomButtonSmall
import com.wisedaimyo.chesstraining.main.components.shimmerBrush
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.AppScreenViewModel
import com.wisedaimyo.chesstraining.main.data.models.CHECK_RATING
import com.wisedaimyo.chesstraining.main.data.models.STUDENT_ZONE
import com.wisedaimyo.chesstraining.main.data.models.PLAYED_GAMES
import com.wisedaimyo.chesstraining.main.data.models.PLAY_AGAINST_PLAYER
import com.wisedaimyo.chesstraining.main.data.models.PLAY_ROBOT
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.TRAINER_ZONE
import com.wisedaimyo.chesstraining.main.data.models.model.Invitation
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.DialogPlayAgainstViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.InvitationViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.PlayingGameViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.StudentScreenViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.UsersViewModel
import com.wisedaimyo.chesstraining.main.ui.theme.dimens
import com.wisedaimyo.chesstraining.whoPlays
import java.util.Date

@Composable
fun AppScreen(
    viewModel: StudentScreenViewModel = hiltViewModel(),
    viewmodel: UsersViewModel = hiltViewModel(),
    navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {
        val aiDifficulty = arrayOf("🥉", "🥈", "🥇", "🏅", "🎖️")
        val timeToPlay = arrayOf("5", "10", "15", "20")

        val aiDifficultyNumber = remember { mutableIntStateOf(0) }
        val timeToPlayNumber = remember { mutableIntStateOf(0) }
        val numberOfUnfinishedTasks = remember { mutableIntStateOf(0) }


        LaunchedEffect(key1 = viewModel.chessTasks) {
            for(task in viewModel.chessTasks) {
                if(task.second.done?.contains(Firebase.auth.uid) == false)   {
                    numberOfUnfinishedTasks.intValue = numberOfUnfinishedTasks.intValue + 1
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            TopSection(
                viewmodel.currentUser.displayName,
                viewmodel.currentUser.elo,
                viewmodel.currentUser.photoUrl.toString(),
                getImageResId(context = LocalContext.current, viewmodel.currentUser.image ?: "ic_launcher_foreground" )
                )

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.medium1),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.dimens.medium1)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            if (timeToPlayNumber.value < 3) timeToPlayNumber.value += 1 else timeToPlayNumber.value =
                                0
                        }
                        .background(Color.Green)
                        .padding(horizontal = 20.dp)
                        .padding(vertical = 5.dp)

                ) {

                    Text(
                        timeToPlay[timeToPlayNumber.value],
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Black,
                        )

                    Text(
                        "min",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = Black,
                        )
                }

                Text(text = "🐣",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.displayLarge.fontSize,
                    color = Black
                    )


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.dimens.medium1)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            if (aiDifficultyNumber.value < 4) aiDifficultyNumber.value += 1 else aiDifficultyNumber.value =
                                0
                        }
                        .background(Color.Magenta)
                        .padding(horizontal = 10.dp)
                        .padding(vertical = 5.dp)
                ) {

                    Text(
                        aiDifficulty[aiDifficultyNumber.value],
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        color = Black
                    )
                }
            }

            MenuSection(trener = viewmodel.currentUser.isTrainer, numberOfUnfinishedTasks, navController, timeToPlayNumber, aiDifficultyNumber, eloPlayer = viewmodel.currentUser.elo)

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    LaunchedEffect(key1 = viewmodel.currentUser) {
        if(viewmodel.currentUser.elo == null) {
            val user = viewmodel.currentUser
            user.isTrainer = false
            user.elo = 1200
            user.puzzleElo = 1200
            user.puzzleSolved = 0
            user.puzzleStrike = 0
            Firebase.auth.currentUser?.let { viewmodel.addUserToFirestore(user = user, it.uid) }
        }
    }

}

@Composable
fun MenuSection(trener: Boolean?, numberOfUnfinishedTasks: MutableState<Int>, navController: NavController, timeToPlayNumber: MutableState<Int>, aiDiff: MutableState<Int>, eloPlayer: Int?) {
    val timeToPlay: Array<Long> = arrayOf(60L*5, 60L*10, 60L*15, 60L*20)
    val returnValue: Long = timeToPlay[timeToPlayNumber.value]
    val showDialog = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.dimens.medium1),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showDialog.value) {
            if (eloPlayer != null) {
                DialogPlayAgainstPlayer(showDialog = showDialog, elo = eloPlayer, navController = navController)
            }
        }

        CustomButton(
            stringResource(id = R.string.custom_buttom_play_agaist_player),
            onClick = {
            showDialog.value = true
            })



        CustomButton(stringResource(id = R.string.custom_buttom_play_against_ai),
            onClick = {
                navController.navigate("$PLAY_ROBOT/${returnValue}/${aiDiff.value}") {
                    popUpTo(PLAY_ROBOT) { inclusive = true }
                    println("RESULT: $returnValue")
                    launchSingleTop = true
                }
            })
        CustomButton(stringResource(id = R.string.custom_buttom_check_rating),
            onClick = {
                navController.navigate(CHECK_RATING)
            })

        if (trener == true) {
            CustomButton(stringResource(id = R.string.custom_buttom_trainer_menu),
                onClick = {
                    navController.navigate(TRAINER_ZONE)
                })
        } else {
            CustomButton(stringResource(id = R.string.custom_buttom_trainer_courses),
                numberOfTasks = numberOfUnfinishedTasks.value,
                onClick = {
                    navController.navigate(STUDENT_ZONE)
                })
        }

        CustomButtonSmall(stringResource(id = R.string.custom_buttom_played_games), onClick = {
            navController.navigate(PLAYED_GAMES)
        })
    }
}


@Composable
fun TopSection(playerName: String?, playerElo: Int?, photoUrl: String? = null, image: Int? = null) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenHeightDp
    val imageSize = ((screenWidth / 2.8) / 2).dp

    val showShimmer = remember { mutableStateOf(true) }

    Column{
        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .height((screenHeight / 3).dp)
                .width((screenWidth / 2.5).dp)
                .clip(shape = RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp))
                .background(colorResource(id = R.color.primary)),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (photoUrl.toString() == "null") {
                    if (image == null) {
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                shimmerBrush(
                                    targetValue = 1300f,
                                    showShimmer = showShimmer.value
                                )
                            )
                            .width(imageSize)
                            .heightIn(min = imageSize)
                            .size(imageSize))
                    } else {
                        showShimmer.value = false
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = "",
                            modifier = Modifier
                                .width(imageSize)
                                .heightIn(min = imageSize)
                                .size(imageSize))
                    }
                } else {
                    AsyncImage(
                        model = photoUrl.toString(),
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

                Spacer(modifier = Modifier.width(MaterialTheme.dimens.small2))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.medium2)
                    .height(MaterialTheme.dimens.bigProfileImage)
                ) {
                        Text(modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 10.dp)
                            .background(
                                shimmerBrush(
                                    targetValue = 1300f,
                                    showShimmer = showShimmer.value
                                )
                            ),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            color = White,
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.Green, Color.Yellow, Color.Green)
                                )
                            ),
                            text =  if (playerName != null) playerName else "                      ")


                    Text(modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(y = -5.dp),
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = White,
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Green, Color.Cyan)
                            )
                        ),
                        text = "Ahoj,")

                }

                Text(modifier = Modifier
                    .background(
                        shimmerBrush(
                            targetValue = 1300f,
                            showShimmer = showShimmer.value
                        )),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = White,
                    text = if (playerElo != null) "ELO $playerElo" else "         ")

            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun DialogPlayAgainstPlayer(showDialog: MutableState<Boolean>,
                            viewModel: DialogPlayAgainstViewModel = hiltViewModel(),
                            elo: Int,
                            navController: NavController
) {

    val isMatches = remember { mutableStateOf(false) }
    val isCreateMatch = remember { mutableStateOf(false) }
    val isLookUpForGames = remember { mutableStateOf(false) }

    val context = LocalContext.current

    var numberOfDays = remember { mutableStateOf(0) }
    var isFetched = remember { mutableStateOf(false) }
    var nameOfPlayer = remember { mutableStateOf("") }

    var invitationsList = remember { mutableStateListOf<Invitation>() }

    LaunchedEffect(key1 = viewModel.inviteUserToGameResponse) {
        when(viewModel.inviteUserToGameResponse) {
            is Response.Success -> { Toast.makeText(context, "Hráč bol pozvaný", Toast.LENGTH_SHORT).show() }
            else -> { println("INVITATION ${viewModel.inviteUserToGameResponse}")  }
        }
    }

    LaunchedEffect(key1 = viewModel.inviteUserWithNameToGameResponse) {
        when(viewModel.inviteUserWithNameToGameResponse) {
            is Response.Success -> { Toast.makeText(context, "Hráč bol pozvaný", Toast.LENGTH_SHORT).show() }
            else -> { println("INVITATION ${viewModel.inviteUserWithNameToGameResponse}")  }
        }
    }

    LaunchedEffect(key1 = viewModel.addNewGameWithUsers) {
        when(viewModel.addNewGameWithUsers) {
            is Response.Success -> { Toast.makeText(context, "Prijal si výzvu", Toast.LENGTH_SHORT).show() }
            else -> { }
        }
    }

    LaunchedEffect(key1 = viewModel.getAllGamesForUser) {
        when(viewModel.getAllGamesForUser) {
            is Response.Success -> {  }
            else -> { }
        }
    }



    LaunchedEffect(key1 = viewModel.getAllInvitations) {
        when (val response = viewModel.getAllInvitations) {
            is Response.Success -> {
                invitationsList = mutableStateListOf()
                response.data?.let {
                    invitationsList.addAll(it)
                    val userList = mutableListOf<String>()
                    for (invitation in invitationsList) {
                        println("")
                        userList.add(invitation.whoUserId.toString())
                    }
                    if(userList.isNotEmpty())
                        viewModel.getUsersWithIds(userList)
                }
            }
            else -> {  }
        }
    }

    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
            isFetched.value = false
        },
        title = {
            Text(text = "Menu",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Black
            )
            if(!isFetched.value) {
                println("GAME !!")
                Firebase.auth.uid?.let { viewModel.getAllGamesForUser(it) }
                Firebase.auth.uid?.let { viewModel.getAllInvitations(it) }
                isFetched.value = true
            }
        },
        text = {
                if (!isMatches.value && !isCreateMatch.value && !isLookUpForGames.value)
                    Text(text = "Zoznam možností.")

                if (isCreateMatch.value) {
                    Column {
                        Text(text = "Nastav čas na ťah.")

                        Spacer(modifier = Modifier.padding(vertical = 5.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { numberOfDays.value = 1 },
                                colors = ButtonDefaults.buttonColors(if (numberOfDays.value == 1) Color.Blue else Color.Gray)
                            ) {
                                Text(text = "1 deň")
                            }
                            Button(
                                onClick = { numberOfDays.value = 2 },
                                colors = ButtonDefaults.buttonColors(if (numberOfDays.value == 2) Color.Blue else Color.Gray)
                            ) {
                                Text(text = "2 dni")
                            }
                            Button(
                                onClick = { numberOfDays.value = 3 },
                                colors = ButtonDefaults.buttonColors(if (numberOfDays.value == 3) Color.Blue else Color.Gray)
                            ) {
                                Text(text = "3 dni")
                            }

                        }

                        Spacer(modifier = Modifier.padding(vertical = 5.dp))

                        Text(text = "Nastav meno hráča.")
                        TextField(
                            value = nameOfPlayer.value,
                            onValueChange = { nameOfPlayer.value = it },
                            placeholder = { Text("Voliteľné") }
                        )
                    }
                }

                if (isMatches.value) {
                    if (viewModel.opponentList.size != 0) {
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 2.dp)

                        ) {
                            if (viewModel.opponentList.size == viewModel.gamesList.size)
                                for (index in 0..<viewModel.gamesList.size) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .background(colorResource(id = R.color.light_board))
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    viewModel.currentUser.displayName?.let {
                                        Text(text = it,
                                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Black,
                                            ) }
                                    Text(text = "VS",
                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = White)
                                    Text(text = viewModel.opponentList.get(index),
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Black,
                                        )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                )
                                {
                                    val player = if(viewModel.gamesList[index].white == viewModel.currentUID) "White" else "Black"

                                        Button(onClick = {
                                            navController.navigate("$PLAY_AGAINST_PLAYER/${viewModel.idList[index]}")
                                        }) {
                                            Text(text =
                                            if(viewModel.gamesList[index].fen?.let { whoPlays(it) } == player)
                                                "Tvoj ťah"
                                            else
                                                "Ťah oponenta"
                                            )
                                        }

                                    Text(
                                        text = "Zostáva čas:",
                                        textAlign = TextAlign.Center
                                    )

                                    Text(text = "${viewModel.gamesList[index].lastMove?.let {
                                        viewModel.gamesList[index].time?.let { it1 ->
                                            calculateRemainingTime(
                                                it, it1
                                            )
                                        }
                                    }}", textAlign = TextAlign.Center,
                                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Black,
                                        )
                                }
                            }
                            }
                        }
                    } else {
                        Text("Momentálne nemáš žiadnu odohratú partiu")
                    }
                }
        },
        buttons = {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {

                if(!isMatches.value && !isCreateMatch.value && !isLookUpForGames.value) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isMatches.value = true }
                    ) {
                        Text("Aktuálne partie")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            isLookUpForGames.value = true
                            isMatches.value =false
                        }
                    ) {
                        Text("Zobraz výzvy")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isCreateMatch.value = true }
                    ) {
                        Text("Vytvor partiu")
                    }
                }


                if(isLookUpForGames.value) {
                    Column (
                        Modifier.verticalScroll(rememberScrollState())
                    ){
                    if(invitationsList.size == viewModel.usersList.size)
                       for (invite in 0..<viewModel.usersList.size) {
                           Row(
                               modifier = Modifier
                                   .padding(vertical = 10.dp)
                                   .fillMaxWidth(),
                               verticalAlignment = Alignment.CenterVertically,
                               horizontalArrangement = Arrangement.SpaceEvenly
                           ) {
                               Column {
                                   Text(viewModel.usersList.get(invite).first,
                                       fontWeight = FontWeight.SemiBold,
                                       fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                       color = Black
                                       )
                                   Text(viewModel.usersList.get(invite).second,
                                       fontWeight = FontWeight.SemiBold,
                                       fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                       color = Color.DarkGray)
                               }
                               invitationsList.get(invite).time?.let {
                                   Text(text = "${it} hodín",
                                       fontWeight = FontWeight.SemiBold,
                                       fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                       color = Black
                                   ) }

                               Column(
                                   horizontalAlignment = Alignment.CenterHorizontally
                               ) {
                                   Button(
                                       onClick = {
                                           invitationsList.get(invite).whoUserId?.let {
                                               invitationsList.get(invite).toUserId?.let { it1 ->
                                                   invitationsList.get(invite).time?.let { it2 ->
                                                       viewModel.addNewGameForUsers(
                                                           it,
                                                           it1,
                                                           it2
                                                       )
                                                   }
                                               }
                                           }

                                           invitationsList.get(invite).toUserId?.let {
                                               invitationsList.get(invite).whoUserId?.let { it1 ->
                                                   invitationsList.get(invite).time?.let { it2 ->
                                                       viewModel.removeGivenInvitation(
                                                           it,
                                                           it1,
                                                           it2,
                                                       )
                                                   }
                                               }
                                           }

                                           isFetched.value = false
                                           isLookUpForGames.value = false

                                       }) {
                                       Text("   Prijať   ")
                                   }

                                   Button(onClick = {
                                       invitationsList.get(invite).toUserId?.let {
                                           invitationsList.get(invite).whoUserId?.let { it1 ->
                                               invitationsList.get(invite).time?.let { it2 ->
                                                   viewModel.removeGivenInvitation(
                                                       it,
                                                       it1,
                                                       it2,
                                                   )
                                               }
                                           }
                                       }
                                       isFetched.value = false
                                   }) {
                                       Text("Odmietnut")
                                   }

                               }
                           }

                           Divider()

                       }
                   }
                }

                if (isCreateMatch.value) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if(numberOfDays.value != 0)
                            if(nameOfPlayer.value.isNotEmpty()) {
                                viewModel.inviteUserWithNameToGame(nameOfPlayer.value, time = (numberOfDays.value*24))
                            } else {
                                viewModel.currentUser.displayName?.let { viewModel.inviteUserToGame(elo, time = (numberOfDays.value*24), currentUserName = it) }
                            }
                        }
                    ) {
                        Text("Vytor partiu")
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


@Preview(showBackground = true)
@Composable
fun PreviewAppScreen() {
    AppScreen(navController = rememberNavController())
}