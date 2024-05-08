package com.wisedaimyo.chesstraining.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ChessEvaluationBar(evaluation: StateFlow<Float>, leadingSide: String) {
    val eval by evaluation.collectAsState(initial = 0f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.CenterStart
    ) {
        val progress = if(leadingSide=="black") (-eval + 10.0f) / 20.0f else (eval + 10.0f) / 20.0f
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(24.dp)
                .background(if (leadingSide == "white") Color.White else Color.Black)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.001f)
                .height(24.dp)
                .align(Alignment.Center)
                .background(Color.Red)
        )
        Text(
            text = "Hodnotenie: $eval",
            modifier = Modifier.align(Alignment.Center),
            color = if (leadingSide == "white") Color.Black else Color.Yellow,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun PreviewChessEvaluationBar() {
    val evaluation = MutableStateFlow(4f)
    ChessEvaluationBar(evaluation = evaluation, leadingSide = "white")
}


