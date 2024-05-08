package com.wisedaimyo.chesstraining.main.screens.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.Timestamp
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.getDrawableName
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.CustomButton
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.SignUpViewModel

@Composable
fun RegisterFinishScreen(
    navigateBack: () -> Unit,
    isTrainer: Boolean = false,
    image: Int = R.drawable.king_black,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    var name by rememberSaveable(
        stateSaver = TextFieldValue.Saver,
        init = {
            mutableStateOf(
                value = TextFieldValue(
                    text = ""
                )
            )
        }
    )

    var passwordCheck by rememberSaveable(
        stateSaver = TextFieldValue.Saver,
        init = {
            mutableStateOf(
                value = TextFieldValue(
                    text = ""
                )
            )
        }
    )

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {
        val screenWidth = LocalConfiguration.current.screenHeightDp
        val context = LocalContext.current

        Column {
            BackButton {
                navigateBack()
            }

            Column(
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {


                Spacer(modifier = Modifier.padding(vertical = 30.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp))
                        .background(colorResource(id = R.color.primary))
                        .fillMaxWidth()
                        .padding(5.dp)

                ) {

                    Image(
                        painter = painterResource(id = image),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(((screenWidth / 5) / 2).dp)
                            .padding(horizontal = 5.dp)
                    )

                    Spacer(modifier = Modifier.padding(horizontal = 3.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = {
                            Text(
                                text = "Vloz svoje meno",
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.ExtraBold
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )

                }

                OutlinedTextFieldBackground(color = colorResource(id = R.color.primary)) {
                    OutlinedTextField(
                        value = password,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = ""
                            )
                        },
                        //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
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
                            color = Color.White
                        )
                    )
                }

                OutlinedTextFieldBackground(color = colorResource(id = R.color.primary)) {
                    OutlinedTextField(
                        value = passwordCheck,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = ""
                            )
                        },
                        //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                        onValueChange = {
                            passwordCheck = it
                        },
                        label = { Text(text = "Over tvoje heslo") },
                        placeholder = { Text(text = "Over tvoje heslo") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                }

                OutlinedTextFieldBackground(color = colorResource(id = R.color.primary)) {
                    OutlinedTextField(
                        value = email,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = ""
                            )
                        },
                        //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
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

                CustomButton(name = "Zaregistruj sa!",
                    onClick = {
                        if(password.text == passwordCheck.text) {
                            viewModel.getUserWithName(name.text)
                        } else {
                            Toast.makeText(context, "Heslá sa nezhodujú!", Toast.LENGTH_SHORT).show()
                        }
                    })

                LaunchedEffect(key1 = viewModel.getUserWithNameResponse) {
                    when(viewModel.getUserWithNameResponse) {
                        is Response.Failure -> {
                            viewModel.signUpWithEmailAndPassword(email.text, password.text)
                        }
                        is Response.Success-> { Toast.makeText(context, "Meno už existuje!!!", Toast.LENGTH_SHORT).show()}
                        else -> {Toast.makeText(context, "Načitávam", Toast.LENGTH_SHORT).show()}
                    }

                }

                LaunchedEffect(key1 = viewModel.signUpResponse) {
                    when(viewModel.signUpResponse) {
                        is Response.Success -> {
                            if(password.text == passwordCheck.text && name.text != "" && email.text != "") {
                                Firebase.auth.currentUser?.let {
                                    viewModel.addUserToFirestore(
                                        User(
                                            createdAt = Timestamp.now(),
                                            email = email.text,
                                            displayName = name.text,
                                            isTrainer = isTrainer,
                                            image = getDrawableName(context, image),
                                            photoUrl = null,
                                            elo = 1200,
                                            puzzleElo = 1200,
                                            puzzleSolved = 0,
                                            puzzleStrike = 0
                                        ),
                                        it.uid
                                    )
                                }
                            }

                        }
                        else -> { Toast.makeText(context, "Chyba! skús zmeniť údaje.", Toast.LENGTH_SHORT).show() }
                    }
                }

                SendEmailVerification()
            }
        }
    }
}

@Composable
fun SendEmailVerification(
    viewModel: SignUpViewModel = hiltViewModel()
) {
    when(val sendEmailVerificationResponse = viewModel.sendEmailVerificationResponse) {
        is Response.Loading -> Text("Loading")
        is Response.Success -> Unit
        is Response.Failure -> sendEmailVerificationResponse.apply {
            LaunchedEffect(e) {
                print(e)
            }
        }
    }
}


@Composable
fun OutlinedTextFieldBackground(
    color: Color,
    content: @Composable () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp)
                .background(
                    color,
                    shape = RoundedCornerShape(4.dp)
                )
        )
        content()
    }
}