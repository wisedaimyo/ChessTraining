package com.wisedaimyo.chesstraining.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wisedaimyo.chesstraining.R

@Composable
fun LostConnection() {
    Column {
        Spacer(modifier = Modifier.padding(vertical = 45.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .background(
                    colorResource(id = R.color.primary),
                    shape = RoundedCornerShape(10.dp)
                )
                .fillMaxWidth()

        ) {

            Text(
                text = "🛜",
                fontSize = 45.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nie je pripojenie k internetu",
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 35.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}