package com.wisedaimyo.chesstraining.main.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.CustomButton
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.SignInViewModel
import androidx.compose.ui.platform.LocalContext
import com.wisedaimyo.chesstraining.Utils.Companion.showMessage

@Composable
fun LoginScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
   var email by rememberSaveable(
       stateSaver = TextFieldValue.Saver,
       init = {
            mutableStateOf(
                value = TextFieldValue(
                    text = ""
                )
            )
       }
   )

    var password by rememberSaveable(
        stateSaver = TextFieldValue.Saver,
        init = {
            mutableStateOf(
                value = TextFieldValue(
                    text = ""
                )
            )
        }
    )

    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current


    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {

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

                    Spacer(modifier = Modifier.padding(top = 15.dp))

                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Registruj sa"
                    )

                    Text(
                        "Prihlas sa",
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.padding(top = 35.dp))

            OutlinedTextFieldBackground(color = colorResource(id = R.color.primary)) {
                OutlinedTextField(
                    value = email,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = ""
                        )
                    },
                    onValueChange = {
                        email = it
                    },
                    label = { Text(text = "Zadaj svoj email") },
                    placeholder = { Text(text = "Zadaj svoj email") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.padding(top = 5.dp))

                OutlinedTextFieldBackground(color = colorResource(id = R.color.primary)) {
                    OutlinedTextField(
                        value = password,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = ""
                            )
                        },
                         onValueChange = {
                            password = it
                        },
                        label = { Text(text = "Vloz tvoje heslo") },
                        placeholder = { Text(text = "Vloz tvoje heslo") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White)
                    )
                }

            Spacer(modifier = Modifier.padding(top = 5.dp))

            CustomButton(name = "Prihlas sa!",
                onClick = {
                    keyboard?.hide()
                    viewModel.signInWithEmailAndPassword(
                        email.text, password.text
                    )
                })

            CustomButton(name = "Nepamätám si heslo!",
                onClick = {
                    keyboard?.hide()
                    viewModel.forgotPassword(email.text)
                })


            Row {
                SignIn(showErrorMessage = {errorMessage -> showMessage(context, errorMessage)  })
            }


            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight()
            ) {
                BackButton {
                    navigateBack()
                }
            }

        }
    }
 }


@Composable
fun SignIn(
    viewModel: SignInViewModel = hiltViewModel(),
    showErrorMessage: (errorMessage: String?) -> Unit
) {
    when(val signInResponse = viewModel.signInResponse) {
        is Response.Loading -> Text("Načítavam")
        is Response.Success -> Unit
        is Response.Failure -> signInResponse.apply {
            LaunchedEffect(e) {
                print(e)
                showErrorMessage(e?.message)
            }
        }
    }
}