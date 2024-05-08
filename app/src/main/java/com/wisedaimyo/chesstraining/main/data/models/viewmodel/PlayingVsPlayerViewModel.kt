package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.PlayedGame
import com.wisedaimyo.chesstraining.main.data.models.model.PlayingGame
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.repository.AddPlayedGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.PlayedGameRepository
import com.wisedaimyo.chesstraining.main.data.repository.PlayingGameRepository
import com.wisedaimyo.chesstraining.main.data.repository.RemoveGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.UpdateEloResponse
import com.wisedaimyo.chesstraining.main.data.repository.UpdateGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingVsPlayerViewModel @Inject constructor(
    private val repo: PlayingGameRepository,
    private val repo2: UserRepository,
    private val repo3: PlayedGameRepository
): ViewModel() {

    var updateGameResponse by mutableStateOf<UpdateGameResponse>(Response.Loading)
        private set

    var addPlayedGameResponse by mutableStateOf<AddPlayedGameResponse>(Response.Loading)
        private set

    var updateEloWhite by mutableStateOf<UpdateEloResponse>(Response.Loading)
        private set

    var updateEloBlack by mutableStateOf<UpdateEloResponse>(Response.Loading)
        private set

    var removePlayingGame by mutableStateOf<RemoveGameResponse>(Response.Loading)
        private set

    var chosenGame by mutableStateOf(mutableStateOf(PlayingGame()))

    var currentUser by mutableStateOf(mutableStateOf(User()))
    var whitePlayer by mutableStateOf(mutableStateOf(User()))
    var blackPlayer by mutableStateOf(mutableStateOf(User()))


    fun updateEloWhite(playerId: String, elo: Int) = viewModelScope.launch {
        updateEloWhite = Response.Loading
        updateEloWhite = repo2.updateEloToUserToFirestore(playerId, elo)
    }

    fun updateEloBlack(playerId: String, elo: Int) = viewModelScope.launch {
        updateEloBlack = Response.Loading
        updateEloBlack = repo2.updateEloToUserToFirestore(playerId, elo)
    }

    @SuppressLint("SuspiciousIndentation")
    fun getGameWithId(gameId: String) = viewModelScope.launch {
        if(chosenGame.value.white == null)
        when(val response = repo.getGameWithId(gameId)) {
            is Response.Success -> {
                chosenGame.value = response.data!!
            }
            else -> { }
        }
    }

    fun updateGame(gameId: String, game: PlayingGame) = viewModelScope.launch {
        updateGameResponse = Response.Loading
        updateGameResponse = repo.updateGame(gameId, game)
    }

    fun addPlayedGame(playedGame: PlayedGame, gameId: String, whiteId: String, eloWhite: Int, blackId: String, eloBlack: Int) = viewModelScope.launch {
        addPlayedGameResponse = Response.Loading
        addPlayedGameResponse = repo3.addPlayedGame(playedGame)

        if (addPlayedGameResponse == Response.Success(true)) {
            updateEloWhite(whiteId, eloWhite)
            updateEloBlack(blackId, eloBlack)
            removePlayingGame(gameId)
        }
    }

    private fun removePlayingGame(gameId: String) = viewModelScope.launch {
        removePlayingGame = Response.Loading
        removePlayingGame = repo.removeGame(gameId)
    }

    fun getPlayersForGame(game: PlayingGame) = viewModelScope.launch {
        when(val response = Firebase.auth.uid?.let { repo2.getCurrentUserFromFirestore(it) }) {
            is Response.Success -> {
                currentUser.value = response.data!!
            }
            else -> { }
        }

        when(val response = game.white?.let { repo2.getCurrentUserFromFirestore(it) }) {
            is Response.Success -> {
                whitePlayer.value = response.data!!
            }
            else -> { }
        }
        when(val response = game.black?.let { repo2.getCurrentUserFromFirestore(it) }) {
            is Response.Success -> {
                blackPlayer.value = response.data!!
            }
            else -> { }
        }
    }
}