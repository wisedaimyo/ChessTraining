package com.wisedaimyo.chesstraining.main.data.models.viewmodel

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
import com.wisedaimyo.chesstraining.main.data.repository.PlayedGameRepository
import com.wisedaimyo.chesstraining.main.data.repository.PlayingGameRepository
import com.wisedaimyo.chesstraining.main.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyzeCheckGameEditViewModel @Inject constructor(
    private val repo: UserRepository,
    private val repo1: PlayedGameRepository
): ViewModel() {

    var chosenGame by mutableStateOf(mutableStateOf(PlayedGame()))

    var currentUser by mutableStateOf(mutableStateOf(User()))
    var whitePlayer by mutableStateOf(mutableStateOf(User()))
    var blackPlayer by mutableStateOf(mutableStateOf(User()))

    fun getChosenGame(gameId: String) = viewModelScope.launch {
        if(chosenGame.value.white == null)
            when(val response = repo1.getPlayedGameWithId(gameId)) {
                is Response.Success -> {
                    chosenGame.value = response.data!!
                }
                else -> { }
            }
    }

    fun getPlayersForGame(game: PlayedGame) = viewModelScope.launch {
        when(val response = Firebase.auth.uid?.let { repo.getCurrentUserFromFirestore(it) }) {
            is Response.Success -> {
                currentUser.value = response.data!!
            }
            else -> { }
        }

        when(val response = game.white?.let { repo.getCurrentUserFromFirestore(it) }) {
            is Response.Success -> {
                whitePlayer.value = response.data!!
            }
            else -> { }
        }

        when(val response = game.black?.let { repo.getCurrentUserFromFirestore(it) }) {
            is Response.Success -> {
                blackPlayer.value = response.data!!
            }
            else -> { }
        }
    }




}