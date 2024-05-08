package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.UseCaseUsers
import com.wisedaimyo.chesstraining.main.data.repository.AuthRepository
import com.wisedaimyo.chesstraining.main.data.repository.GetUserWithNameResponse
import com.wisedaimyo.chesstraining.main.data.repository.ResetEloResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val useCaseUsers: UseCaseUsers
): ViewModel() {
    var auth = Firebase.auth.currentUser

    var currentUser by mutableStateOf(User())
        private set

    var getUserWithNameResponse by mutableStateOf<GetUserWithNameResponse>(Response.Loading)
        private set

    var resetEloResponse by mutableStateOf<ResetEloResponse>(Response.Loading)
        private set

    init {
        auth?.let { getCurrentUser(it.uid) }
    }

    fun resetElo(userId: String) = viewModelScope.launch {
        resetEloResponse = Response.Loading
        resetEloResponse = useCaseUsers.resetElo(userId)
    }

    fun getUserWithName(userName: String) = viewModelScope.launch {
        getUserWithNameResponse = Response.Loading
        getUserWithNameResponse = useCaseUsers.getUserWithName(userName)
    }

     fun getCurrentUser(userId: String) = viewModelScope.launch {
        when(val response = useCaseUsers.getCurrentUser(userId = userId)) {
            is Response.Success -> currentUser = response.data!!
            else -> {}
        }
    }

    fun signOut() = repo.signOut()

    suspend fun removeUser() = viewModelScope.launch { repo.removeUser() }
}