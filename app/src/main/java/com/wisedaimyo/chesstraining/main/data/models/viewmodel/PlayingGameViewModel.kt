package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.PlayingGame
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.UseCaseUsers
import com.wisedaimyo.chesstraining.main.data.repository.CreateGameForUsersResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetGamesForUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.PlayingGameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlayingGameViewModel @Inject constructor(
    private val repo: PlayingGameRepository
): ViewModel() {
    var addNewGameWithUsers by mutableStateOf<CreateGameForUsersResponse>(Response.Loading)
        private set
    var getAllGamesForUser by mutableStateOf<GetGamesForUserResponse>(Response.Loading)
        private set

    var chosenGame by mutableStateOf(mutableStateOf<PlayingGame>(PlayingGame()))
    var gamesList by mutableStateOf(mutableListOf<PlayingGame>())


    fun getGameWithId(gameId: String) = viewModelScope.launch {
        when(val response = repo.getGameWithId(gameId)) {
            is Response.Success -> {
                chosenGame.value = response.data!!
            }
            else -> { }
        }
    }

    fun getAllGamesForUser(userId: String) = viewModelScope.launch {
        when(val response = repo.getGamesForUser(userId)) {
            is Response.Success -> {
                gamesList = mutableListOf()
                for(game in response.data!!) {
                    gamesList.add(game.second)
                }
            }
            else -> { }
        }
    }

    fun addNewGameForUsers(firstUser: String, secondUser: String, time: Int) = viewModelScope.launch {
        addNewGameWithUsers = Response.Loading
        addNewGameWithUsers = repo.createGameForUsers(firstUser, secondUser, time)
    }


}