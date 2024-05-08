package com.wisedaimyo.chesstraining.main.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.wisedaimyo.chesstraining.R

@Composable
fun Custom_Card(
    isFinihed: Boolean = false,
    name: String = "Puzzle - Tazke",
    image: Int = R.drawable.ic_launcher_foreground,
    onClick: () -> Unit
                ) {
    val color: Color
    if (isFinihed == true) {
        color = Color.Green
    } else {
        color = MaterialTheme.colorScheme.onPrimary
    }

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(10.dp, 5.dp, 10.dp, 10.dp)
                .height(150.dp)
                .width(150.dp)
                ,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp,
            ),
            colors = CardDefaults.cardColors(
                containerColor = color,
            ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .clickable(onClick = onClick)
            ) {

                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(150.dp)
                        .height(130.dp)
                )

                Text(
                    name,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    color = Color.Black
                )

        }
    }
}


