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
import com.wisedaimyo.chesstraining.main.data.repository.GetPlayedGameForUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.InvitationRepository
import com.wisedaimyo.chesstraining.main.data.repository.PlayedGameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayedGamesViewModel @Inject constructor(
    private val repo: PlayedGameRepository
): ViewModel() {

    init {
        Firebase.auth.uid?.let { getPlayedGamesByUser(it) }
    }

    var gamesList by mutableStateOf(listOf<Pair<String, PlayedGame>>())



    private fun getPlayedGamesByUser(userId: String) = viewModelScope.launch {
        when(val response = repo.getPlayedGamesForUser(userId)) {
            is Response.Success -> {
                val tempList = mutableListOf<Pair<String, PlayedGame>>()
                for(game in response.data!!) {
                    tempList.add(Pair(game.first, game.second))
                }
                gamesList = tempList.toList()
            }
            else -> {}
        }
    }



}