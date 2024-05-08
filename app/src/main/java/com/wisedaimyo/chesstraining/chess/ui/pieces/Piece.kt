package com.wisedaimyo.chesstraining.chess.ui.pieces

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.wisedaimyo.chesstraining.chess.model.pieces.Piece

@Composable
fun Piece(
    piece: Piece,
    squareSize: Dp,
    modifier: Modifier = Modifier
) {
    key(piece) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.size(squareSize, squareSize)
        ) {
            piece.asset?.let {
                Icon(
                    painter = painterResource(id = it),
                    tint = Color.Unspecified,
                    contentDescription = "${piece.setPiece} ${piece.javaClass.simpleName}"
                )
            } ?: run {
                Text(
                    text = piece.symbol,
                    color = Color.Black,
                    fontSize = with(LocalDensity.current) {
                        (squareSize / 5 * 4).toSp()
                    }
                )
            }
        }
    }
}
