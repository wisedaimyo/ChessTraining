package com.wisedaimyo.chesstraining.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.ui.theme.dimens

@Composable
fun CustomButtonSmall(name: String, onClick: () -> Unit) {
    val screenWidth = LocalConfiguration.current.screenHeightDp
    Column (
        modifier = Modifier.padding(top = MaterialTheme.dimens.small2)
    ){
        Button(
            modifier = Modifier
                .width(width = (screenWidth / 5).dp)
                .shadow(elevation = 4.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor =
                colorResource(
                    id = R.color.secondary
                ),
                contentColor = Color.Red),
            onClick = onClick
        ) {
            Text(name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun CustomButtonSmall_Preview() {
    CustomButtonSmall(name = "Test", onClick = { })
}
