package com.wisedaimyo.chesstraining.main.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wisedaimyo.chesstraining.R

@Preview
@Composable
fun AnimatedBallToArrow() {
    val transition = rememberInfiniteTransition()
    val position by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = Dp(-position * 400))
            ) {
                drawCircle(color = Color(0xFF698448))
            }
            Icon(
                Icons.Default.Check, contentDescription = null,
                tint = Color.Green ,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = Dp(-position * 400))
                    .size(100.dp)
            )
    }
}
