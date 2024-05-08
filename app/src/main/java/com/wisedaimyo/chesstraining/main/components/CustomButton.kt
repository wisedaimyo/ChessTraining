package com.wisedaimyo.chesstraining.main.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.annotations.Until
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.ui.theme.dimens

@SuppressLint("SuspiciousIndentation")
@Composable
fun CustomButton(
    name: String,
    buttonColor: Int = R.color.secondary,
    numberOfTasks: Int = 0,
    onClick: () -> Unit) {
    Box(
        Modifier.padding(top = 13.dp)
    ) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 10.dp),

        shape = RectangleShape,

        colors = ButtonDefaults.buttonColors(
            containerColor =
            colorResource(
                id = buttonColor
            ),
            contentColor = Color.Green),
        onClick = onClick

    ) {

        Text(
            name,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }

        if (numberOfTasks != 0)
            Box(
                modifier = Modifier
                    .offset(x = 15.dp, y =-5.dp)
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(40.dp))
                    .size(35.dp)
                    .background(Color.Red)
            ) {
                Text(
                    text = "$numberOfTasks",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)

                )
            }

    }
}

@Preview
@Composable
fun Custom_buttom_preview() {
    Column {

        CustomButton(name = "Blabla", numberOfTasks = 14) {

        }

    }
}
