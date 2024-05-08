package com.wisedaimyo.chesstraining.main.screens.main

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.getDrawableName
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.main.components.CustomButton
import com.wisedaimyo.chesstraining.main.components.shimmerBrush
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.SettingsScreenViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.UsersViewModel
import com.wisedaimyo.chesstraining.main.ui.theme.dimens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsScreen(
    user: UsersViewModel = hiltViewModel(),
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val screenWidth = LocalConfiguration.current.screenHeightDp
    val imageSize = ((screenWidth / 2.8) / 2).dp
    val showShimmer = remember { mutableStateOf(true) }

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

    val isChangeImage = remember { mutableStateOf(false) }
    val isChangeName = remember { mutableStateOf(false) }


    val isResetGameAndPuzzle = remember { mutableStateOf(false) }
    val isAboutApp = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val MUSIC_KEY = booleanPreferencesKey("music")
    val getMusic: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[MUSIC_KEY]
    }
    val NOTIFICATION_KEY = booleanPreferencesKey("notifications")
    val getNotifications: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATION_KEY]
    }
    val PROMOTION_QUEEN_KEY = booleanPreferencesKey("promotion_queen")
    val getPromotionQueen: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[PROMOTION_QUEEN_KEY]
    }

    val isMusic by getMusic.collectAsState(initial = true)
    val isNotifications by getNotifications.collectAsState(initial = true)
    val isPromotionQueen by getPromotionQueen.collectAsState(initial = true)




    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Box (
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shape),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.padding(vertical = 10.dp))

                    if (viewModel.currentUser.photoUrl == null) {
                        if (viewModel.currentUser.image != null) {
                            Image(
                                painter = painterResource(id = getImageResId(context = LocalContext.current, viewModel.currentUser.image ?: "ic_launcher_foreground" )),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(imageSize)
                                    .padding(top = 30.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.img),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(imageSize)
                                    .padding(top = 30.dp)
                            )
                        }
                    } else {
                        AsyncImage(
                            model = viewModel.currentUser.photoUrl.toString(),
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



                    Spacer(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = stringResource(R.string.close_account),
                            tint = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 3.dp),
                        )

                        if(viewModel.currentUser.displayName!= null) {
                            Text(
                                viewModel.currentUser.displayName!!,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        } else {
                            Text(
                                "ANONYM",
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(vertical = 10.dp))

                    if(isChangeImage.value) {
                        ModalBottomSheet(onDismissRequest = { isChangeImage.value = false }) {
                            Column {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 15.dp)
                                ) {


                                    Text(
                                        text = "Vyber si Avatar",
                                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )

                                    Spacer(modifier = Modifier.padding(vertical = 15.dp))

                                    Text(
                                        "Zoznam obrázkov",
                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
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
                                                        user.auth?.let {
                                                            user.changeImage(
                                                                it.uid,
                                                                getDrawableName(context, x) ?: "dragon"
                                                            )
                                                        }
                                                        user.auth?.let { viewModel.getCurrentUser(it.uid) }
                                                        isChangeImage.value = false
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
                                                        user.auth?.let {
                                                            user.changeImage(
                                                                it.uid,
                                                                getDrawableName(context, x) ?: "dragon"
                                                            )
                                                        }
                                                        user.auth?.let { viewModel.getCurrentUser(it.uid) }
                                                        isChangeImage.value = false
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
                                                        user.auth?.let {
                                                            user.changeImage(
                                                                it.uid,
                                                                getDrawableName(context, x) ?: "dragon"
                                                            )
                                                        }
                                                        user.auth?.let { viewModel.getCurrentUser(it.uid) }
                                                        isChangeImage.value = false
                                                    }
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color.White)

                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.padding(vertical = 30.dp))

                                }
                            }
                        }
                    }
                    if(isChangeName.value) {
                            Dialog(onDismissRequest = { isChangeName.value = false }) {
                                viewModel.auth?.let {
                                    ChangeNameScreen(
                                        isChangeName,
                                        userVM = user,
                                        viewModel = viewModel,
                                        userId = it.uid)
                                }
                            }
                    }

                    Row {
                        Button(
                            onClick = {
                                isChangeImage.value = true
                            }
                        ) {
                            Text(stringResource(R.string.change_photo))
                        }

                        Spacer(modifier = Modifier.padding(horizontal = 5.dp))

                        Button(
                            onClick = {
                                isChangeName.value = true
                            }
                        ) {
                            Text(stringResource(R.string.change_name))
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 20.dp))

                    Text("ELO ${viewModel.currentUser.elo}",
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Green
                        )
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { viewModel.viewModelScope.launch {  viewModel.removeUser() } }
            ){
                Icon(imageVector = Icons.Default.Close,
                    contentDescription = "Close account",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 2.dp),
                    )

                Text(
                    stringResource(R.string.delete_account),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                )
            }

            Column(

                modifier = Modifier.padding(horizontal = 10.dp)

            ) {

                CustomButton(name = stringResource(R.string.games_and_puzzles),
                    onClick = {
                        isResetGameAndPuzzle.value = true
                    })

                if(isResetGameAndPuzzle.value==true) {
                    Dialog(onDismissRequest = { isResetGameAndPuzzle.value = false }) {
                        Column(
                            Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.White)
                                .padding(20.dp)
                        ){
                            Text(text = "Zoznam možností")

                            Button(onClick = {
                                viewModel.auth?.let { viewModel.resetElo(it.uid) }
                                Toast.makeText(context, "Hádankové ELO zresetované.", Toast.LENGTH_LONG).show()
                                isResetGameAndPuzzle.value = false},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Reset ELO hádanky")
                            }

                            Button(onClick = {
                                coroutineScope.launch {
                                    context.dataStore.edit { settings ->
                                        settings.set(PROMOTION_QUEEN_KEY, if(isPromotionQueen == true) false else true)
                                    }
                                }
                                val value = if(isPromotionQueen == true) "Zapnuté" else "Vypnuté"
                                Toast.makeText(context, "Promócia dámy: ${value}", Toast.LENGTH_SHORT).show()
                                isResetGameAndPuzzle.value = false
                                             },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Vždy promócia dámy")
                            }

                            Button(onClick = { isResetGameAndPuzzle.value = false},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Zavrieť")
                            }
                        }
                    }

                }

                CustomButton(name = stringResource(R.string.notifications),
                    onClick = {
                        coroutineScope.launch {
                            context.dataStore.edit { settings ->
                                settings.set(NOTIFICATION_KEY, if(isNotifications == true) false else true)
                            }
                        }
                        val value = if(isNotifications == true) "Zapnuté" else "Vypnuté"
                        Toast.makeText(context, "Oznámenia sú: ${value}", Toast.LENGTH_SHORT).show()
                    })

                CustomButton(name = stringResource(R.string.sounds),
                    onClick = {
                        coroutineScope.launch {
                            context.dataStore.edit { settings ->
                                settings.set(MUSIC_KEY, if(isMusic == true) false else true)
                            }
                        }
                        val value = if(isMusic == true) "Zapnuté" else "Vypnuté"
                        Toast.makeText(context, "Zvuky sú sú: ${value}", Toast.LENGTH_SHORT).show()
                    })

                CustomButton(name = stringResource(R.string.about),
                    onClick = {
                        isAboutApp.value = true
                    })

                if(isAboutApp.value == true)
                Dialog(onDismissRequest = { isAboutApp.value = false }) {
                    Column(
                        Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.White)
                            .padding(20.dp)
                    ){
                        Text(
                            text = "O aplikácií",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                            )

                        Spacer(modifier = Modifier.padding(vertical = 7.dp))

                        Column(
                            Modifier
                                .height(200.dp)
                                .verticalScroll(rememberScrollState())
                        ){
                            Text(text = "Táto Android aplikácia, ktorá bola vytvorená ako bakalárska práca, je určená pre šachový tréning. Aplikácia je navrhnutá tak, aby pomohla hráčom zlepšiť svoje šachové schopnosti a porozumieť hlbšie šachovej stratégii. Okrem toho, tréner má možnosť pridávať šachové úlohy, čo umožňuje individualizovaný prístup k tréningu a poskytuje hráčom možnosť riešiť úlohy, ktoré sú priamo spojené s ich šachovými potrebami a cieľmi. Toto rozšírenie funkcionality aplikácie umožňuje trénerom efektívne riadiť proces učenia a poskytovať hráčom konkrétne výzvy na zlepšenie ich hry.")
                        }
                        Spacer(modifier = Modifier.padding(vertical = 7.dp))

                        Button(onClick = { isAboutApp.value = false},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Zavrieť")
                        }
                    }
                }

                CustomButton(name = stringResource(R.string.log_out), buttonColor = R.color.button_other,
                    onClick = {
                        viewModel.signOut()
                    })

            }


            Spacer(modifier = Modifier.padding(40.dp))

        }
    }
}


@Composable
fun ChangeNameScreen(
    isChangeName: MutableState<Boolean>,
    userVM: UsersViewModel,
    viewModel: SettingsScreenViewModel,
    userId: String
) {
    val context = LocalContext.current
    var userName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(key1 = viewModel.getUserWithNameResponse) {
        when(viewModel.getUserWithNameResponse) {
            is Response.Failure -> {
                userVM.changeName(userId, userName)
                viewModel.getCurrentUser(userId)
                Toast.makeText(context, "Meno už zmenené!!", Toast.LENGTH_SHORT).show()
                isChangeName.value = false
            }
            is Response.Success-> { Toast.makeText(context, "Meno už existuje!!!", Toast.LENGTH_SHORT).show()}
            else -> { }
        }

    }

    Column(
        modifier = Modifier
            .padding(30.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
    ){
        Column(
            modifier = Modifier
                .padding(16.dp, 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Zmeň si meno",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = "Tvoje nové meno",
                style = MaterialTheme.typography.bodyLarge
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = userName,
                onValueChange = { userName = it },
                placeholder = { Text(text = "n.p. James Bond") },
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {

                    if(userName != "" && userName.length > 5)
                      viewModel.getUserWithName(userName)
                    else
                        Toast.makeText(context, "Meno musí byť aspoň 5 znakov!", Toast.LENGTH_SHORT).show()

                }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Zmeň meno")
                }
            }

        }
    }
}