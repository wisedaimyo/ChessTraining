package com.wisedaimyo.chesstraining.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Preview
@Composable
fun ShimmeringTaskForStudent() {
    val showShimmer = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                shimmerBrush(
                    targetValue = 1300f,
                    showShimmer = showShimmer.value
                )
            )
    ) {
        Text(
            text = " ",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Text(
            text = " ",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

    }

}

@Preview
@Composable
fun TaskForStudent(title: String = "Hra 4 koni",
                   content: String = "awkfowafowaofaow",
                   time: Date = Date(),
                   isFinished: Boolean = false
                   ) {

    val sdf = SimpleDateFormat("MMM-dd HH:mm", Locale.forLanguageTag("sk"))
    val formattedDate: String = sdf.format(time)

    Column(
        modifier = Modifier
            .fillMaxWidth()
             .padding(horizontal = 20.dp)
             .background(if(isFinished) Color.Green else Color.LightGray)
             .padding(10.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top,
        ) {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.padding(horizontal = 50.dp))

                Text(
                    text = "Dokončiť do: $formattedDate",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
        }

        Text(
            text = content,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            overflow = TextOverflow.Ellipsis,
            maxLines = 5
        )



    }


}