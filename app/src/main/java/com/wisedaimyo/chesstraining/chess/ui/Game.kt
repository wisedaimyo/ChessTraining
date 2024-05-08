package com.wisedaimyo.chesstraining.chess.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import com.wisedaimyo.chesstraining.chess.model.board.Position.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wisedaimyo.chesstraining.chess.model.board.resolutionText
import com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz.ActiveDatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.game.preset.Preset
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameState


@Composable
fun Game(
    state: GamePlayState = GamePlayState(),
    importGameText: String? = null,
    preset: Preset? = null,
) {
    var isFlipped by rememberSaveable { mutableStateOf(false) }
    val gamePlayState = rememberSaveable { mutableStateOf(state) }

    val gameController = remember {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it },
            preset = preset
        )
    }

    CompositionLocalProvider(ActiveDatasetVisualisation provides gamePlayState.value.visualisation) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Board(
                gamePlayState = gamePlayState.value,
                gameController = gameController,
                isFlipped = isFlipped
            )
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Preview(showBackground = true, apiLevel = 33)
@Composable
fun GamePreview() {
    val gamePlayState = GamePlayState()
        Game(
            state = gamePlayState,
        )
}

