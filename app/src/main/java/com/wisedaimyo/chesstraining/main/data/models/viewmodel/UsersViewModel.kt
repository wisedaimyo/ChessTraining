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
import com.wisedaimyo.chesstraining.main.data.repository.AddUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.DeleteUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.UpdateImageResponse
import com.wisedaimyo.chesstraining.main.data.repository.UpdateNameResponse
import com.wisedaimyo.chesstraining.main.data.repository.UsersResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val useCaseUsers: UseCaseUsers
): ViewModel() {
    var auth = Firebase.auth.currentUser

    var userResponse by mutableStateOf<UsersResponse>(Response.Loading)
        private set

    var addUserResponse by mutableStateOf<AddUserResponse>(Response.Loading)
        private set

    var updateImageResponse by mutableStateOf<UpdateImageResponse>(Response.Loading)
        private set

    var updateNameResponse by mutableStateOf<UpdateNameResponse>(Response.Loading)
        private set

    var deleteUserResponse by mutableStateOf<DeleteUserResponse>(Response.Success(false))
        private set

    var currentUser by mutableStateOf(User(elo = 0))
        private set

    var usersList by mutableStateOf(mutableListOf<Pair<String, String>>())

    init {
        auth?.let { getCurrentUser(it.uid) }
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

    fun changeImage(userId: String, image: String) = viewModelScope.launch {
        updateImageResponse = Response.Loading
        updateImageResponse = useCaseUsers.changeImage(userId, image)
    }

    fun changeName(userId: String, name: String) = viewModelScope.launch {
        updateNameResponse = Response.Loading
        updateNameResponse = useCaseUsers.updateName(userId, name)
    }


    fun getCurrentUser(userId: String) = viewModelScope.launch {
        when(val response = useCaseUsers.getCurrentUser(userId = userId)) {
            is Response.Success -> currentUser = response.data!!
            else -> {}
        }
    }

    fun addUserToFirestore(user: User, userId: String) = viewModelScope.launch {
        addUserResponse = Response.Loading
        addUserResponse = useCaseUsers.addUser(user, userId)
    }

    fun getUsers() = viewModelScope.launch {
        useCaseUsers.getUsers().collect { response ->
            userResponse = response
        }
    }

    fun deleteUser(userId: String) = viewModelScope.launch {
        deleteUserResponse = Response.Loading
        deleteUserResponse = useCaseUsers.deleteUser(userId)
    }

}