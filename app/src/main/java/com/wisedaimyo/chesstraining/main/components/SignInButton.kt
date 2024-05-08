package com.wisedaimyo.chesstraining.main.components

import android.hardware.camera2.params.ColorSpaceTransform
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.data.models.SignInState
import com.wisedaimyo.chesstraining.main.ui.theme.dimens

@Composable
fun SignInButton(name: String,
                 logo: Int,
                 color: Color,
                 onClick: () -> Unit) {

    Column (
        modifier = Modifier
            .padding(top = MaterialTheme.dimens.small2)
    ){
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 10.dp),
            shape = RoundedCornerShape(30),

            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = Color.Green),
            onClick = onClick

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(id = logo),
                    contentDescription = "",
                    modifier = Modifier.size(30.dp)
                )

                Text(name,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color =
                    if(color == Color.Black) Color.White else Color.Black
                )

                Text(text = "")
            }

        }
    }
}
