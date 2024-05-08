package com.wisedaimyo.chesstraining.main.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.data.models.FINISH_REGISTER
import com.wisedaimyo.chesstraining.main.data.models.LOGIN
import com.wisedaimyo.chesstraining.main.data.models.LOGIN_REGISTER
import com.wisedaimyo.chesstraining.main.data.models.REGISTER_CHOOSE
import com.wisedaimyo.chesstraining.main.data.models.REGISTER_IMAGE_CHOOSE
import com.wisedaimyo.chesstraining.main.screens.Screen
import com.wisedaimyo.chesstraining.main.ui.theme.dimens


@Composable
fun RegisterChooseScreen(
    navigateToBack: () -> Unit,
    navController: NavController
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
    ) {

        BackButton {
            navigateToBack()
        }

        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Text(
                text = "Kto si?",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row {

                RegisterOptionTeacherOrStudent("Ziak", Icons.Default.Face, onClick = {
                    navController.navigate("${Screen.RegisterImageChoose.route}/${false}")
                })

                Spacer(modifier = Modifier.padding(horizontal = 10.dp))

                RegisterOptionTeacherOrStudent("Ucitel", Icons.Default.Person, onClick = {
                    navController.navigate("${Screen.RegisterImageChoose.route}/${true}")
                })


            }
        }
    }
    }
}


@Composable
fun RegisterOptionTeacherOrStudent(name: String, icon: ImageVector, onClick: () -> Unit) {
    val screenWidth = LocalConfiguration.current.screenHeightDp

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(colorResource(id = R.color.secondary))
            .width((screenWidth / 6).dp)
            .height((screenWidth / 6).dp)
            .shadow(1.dp)
            .clickable(onClick = onClick)
    )  {

        Icon(imageVector = icon,
            contentDescription = "RegisterOption",
            modifier = Modifier.size(
                MaterialTheme.dimens.logoSize
            )
        )

        Text(
            text = name,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
    
}