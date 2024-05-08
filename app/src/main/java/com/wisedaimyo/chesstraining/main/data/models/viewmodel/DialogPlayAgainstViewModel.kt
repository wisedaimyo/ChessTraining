package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.PlayingGame
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.UseCaseUsers
import com.wisedaimyo.chesstraining.main.data.repository.AddUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.CreateGameForUsersResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetDocumentIdInvitationResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetGamesForUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetInvitationForCurrentUser
import com.wisedaimyo.chesstraining.main.data.repository.InvitationRepository
import com.wisedaimyo.chesstraining.main.data.repository.InviteUserToGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.InviteUserWithNameToGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.PlayingGameRepository
import com.wisedaimyo.chesstraining.main.data.repository.RemoveDocumentToWhoResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveInvitationForIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.UsersResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialogPlayAgainstViewModel @Inject constructor(
    private val invRep: InvitationRepository,
    private val playRepo: PlayingGameRepository,
    private val useCaseUsers: UseCaseUsers
): ViewModel() {
    var inviteUserToGameResponse by mutableStateOf<InviteUserToGameResponse>(Response.Loading)
        private set
    var inviteUserWithNameToGameResponse by mutableStateOf<InviteUserWithNameToGameResponse>(
        Response.Loading)
        private set
    var removeDocumentToWhoResponse by mutableStateOf<RemoveDocumentToWhoResponse>(Response.Loading)
        private set
    var getAllInvitations by mutableStateOf<GetInvitationForCurrentUser>(Response.Loading)
        private set
    var addNewGameWithUsers by mutableStateOf<CreateGameForUsersResponse>(Response.Loading)
        private set
    var getAllGamesForUser by mutableStateOf<GetGamesForUserResponse>(Response.Loading)
        private set
    var auth = Firebase.auth.currentUser

    var currentUser by mutableStateOf(User(elo = 0))
        private set

    var currentUID by mutableStateOf("")
        private set

    var usersList by mutableStateOf(mutableListOf<Pair<String, String>>())

    init {
        auth?.let { getCurrentUser(it.uid) }
        currentUID = Firebase.auth.uid.toString()
    }

    fun getUsersWithIds(users: MutableList<String>) = viewModelScope.launch {
        when(val response = useCaseUsers.getUsersWithIds(users)) {
            is Response.Success -> {
                usersList = mutableListOf()
                for(name in response.data!!) {
                    name.displayName?.let { usersList.add(Pair(it, "${name.elo}")) }
                    println("names: ${name.displayName}")
                }
            }
            else -> { }
        }
    }

    fun getCurrentUser(userId: String) = viewModelScope.launch {
        when(val response = useCaseUsers.getCurrentUser(userId = userId)) {
            is Response.Success -> currentUser = response.data!!
            else -> {}
        }
    }

    var gamesList by mutableStateOf(mutableListOf<PlayingGame>())
    var idList by mutableStateOf(mutableListOf<String>())
    var opponentList by mutableStateOf(mutableListOf<String>())

    fun getOpponentForEachGame(user: String) = viewModelScope.launch {
        when(val response = useCaseUsers.getCurrentUser(userId = user)) {
            is Response.Success -> {
                response.data?.let { it.displayName?.let { it1 -> opponentList.add(it1) } }
            }
            else -> {}
        }
    }

    fun getAllGamesForUser(userId: String) = viewModelScope.launch {
        when(val response = playRepo.getGamesForUser(userId)) {
            is Response.Success -> {
                gamesList = mutableListOf()
                opponentList = mutableListOf()
                for(game in response.data!!) {
                    gamesList.add(game.second)
                    idList.add(game.first)
                    (if(userId!=game.second.black) game.second.black else game.second.white)?.let {
                        getOpponentForEachGame(
                            it
                        )
                    }
                }
            }
            else -> { }
        }
    }

    fun addNewGameForUsers(firstUser: String, secondUser: String, time: Int) = viewModelScope.launch {
        addNewGameWithUsers = Response.Loading
        addNewGameWithUsers = playRepo.createGameForUsers(firstUser, secondUser, time)
    }

    fun removeGivenInvitation(toWhoId: String, whoUserId: String, time: Int) = viewModelScope.launch {
        removeDocumentToWhoResponse = Response.Loading
        removeDocumentToWhoResponse = invRep.removeDocumentToWhoWhoUser(toWhoId, whoUserId, time)
    }

    fun getAllInvitations(userId: String) = viewModelScope.launch {
        getAllInvitations = Response.Loading
        getAllInvitations = invRep.getInvitationsForCurrentUser(userId)
    }

    fun inviteUserToGame(userElo: Int, time: Int, currentUserName: String) = viewModelScope.launch {
        inviteUserToGameResponse = Response.Loading
        inviteUserToGameResponse = invRep.inviteUserToGame(userElo, time, currentUserName)
    }

    fun inviteUserWithNameToGame(userName: String, time: Int) = viewModelScope.launch {
        inviteUserWithNameToGameResponse = Response.Loading
        inviteUserWithNameToGameResponse = invRep.inviteUserWithNameToGame(userName, time)
    }

}