package com.wisedaimyo.chesstraining.main.screens.register

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.components.CustomButton
import com.wisedaimyo.chesstraining.main.components.SignInButton
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.AuthGoogleViewModel
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.SignUpViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.UsersViewModel
import com.wisedaimyo.chesstraining.main.ui.theme.dimens


@Composable
fun LoginRegisterScreen(
    viewModel: AuthGoogleViewModel = hiltViewModel(),
    viewmodel: UsersViewModel = hiltViewModel(),
    navigateToSignIn: () -> Unit,
    navigateToRegister: () -> Unit
) {
    var tapped = remember { mutableStateOf(true) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credentials =
                        viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credentials.googleIdToken
                    val googleCredentials = getCredential(googleIdToken, null)
                    viewModel.signInWithGoogle(googleCredentials)

                } catch (it: ApiException) {
                    print(it)
                }
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

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colorResource(id = R.color.background),
        content = {
            Column {
                TopPartComposable()
                Spacer(modifier = Modifier.padding(top = 40.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                ) {

//                    SignInButton(
//                        name = "Prihlás sa s Apple",
//                        logo = R.drawable.applelogo,
//                        color = Color.Black
//                    ) {
//
//                    }

                    SignInButton(
                        name = "Prihlás sa s Googlom",
                        logo = R.drawable.googlelogo,
                        color = Color.White
                    ) {
                        viewModel.oneTapSignIn()
                    }

                    CustomButton(name = "Prihlás sa",
                        onClick = {
                            navigateToSignIn()
                        })

                    Spacer(modifier = Modifier.padding(top = 40.dp))

                    Text(
                        "Si nový?",
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    CustomButton(name = "Zaregistruj sa",
                        onClick = {
                            navigateToRegister()
                        })

                    Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small3))

                    Image(
                        painter = painterResource(
                            id =
                            if (tapped.value) R.drawable.king_white else R.drawable.king_black
                        ),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .clickable(onClick = {
                                tapped.value = !tapped.value
                            })
                    )
                }
            }
        }
    )
    OneTapSignIn(
        launch = {
            launch(it)
        }
    )
}



@Composable
fun TopPartComposable() {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.padding(horizontal = 15.dp)
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

            Spacer(modifier = Modifier.padding(top = 15.dp))

            Icon(imageVector = Icons.Default.AccountCircle,
                contentDescription = "Registruj sa")

            Text(
                "Prihlás sa",
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(10.dp)
            )

        }

    }
}

@Composable
fun OneTapSignIn(
    viewModel: AuthGoogleViewModel = hiltViewModel(),
    launch: (result: BeginSignInResult) -> Unit
) {
    when(val oneTapSignInResponse = viewModel.oneTapSignInResponse) {
        is Response.Loading -> Text(text = "Načítavam")
        is Response.Success -> oneTapSignInResponse.data?.let {
            LaunchedEffect(it) {
                launch(it)
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(oneTapSignInResponse.e)
        }
    }
}
