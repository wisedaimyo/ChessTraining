package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.repository.AuthRepository
import com.wisedaimyo.chesstraining.main.data.repository.SendEmailVerificationResponse
import com.wisedaimyo.chesstraining.main.data.repository.SignUpResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import com.wisedaimyo.chesstraining.main.data.models.Response.*
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.UseCaseUsers
import com.wisedaimyo.chesstraining.main.data.repository.AddUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetUserWithNameResponse
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val useCaseUsers: UseCaseUsers
): ViewModel() {
    var currentUser = Firebase.auth.currentUser

    var addUserResponse by mutableStateOf<AddUserResponse>(Response.Loading)
        private set
    var getUserWithNameResponse by mutableStateOf<GetUserWithNameResponse>(Response.Loading)
        private set
    var signUpResponse by mutableStateOf<SignUpResponse>(Response.Success(false))
        private set
    var sendEmailVerificationResponse by mutableStateOf<SendEmailVerificationResponse>(Success(false))
        private set

    fun signUpWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        signUpResponse = Loading
        signUpResponse = repo.firebaseSignUpWithEmailAndPassword(email, password)
    }

    fun getUserWithName(userName: String) = viewModelScope.launch {
        getUserWithNameResponse = Loading
        getUserWithNameResponse = useCaseUsers.getUserWithName(userName)
    }

    fun addUserToFirestore(user: User, userId: String) = viewModelScope.launch {
        addUserResponse = Response.Loading
        addUserResponse = useCaseUsers.addUser(user, userId)
    }

    fun sendEmailVerification() = viewModelScope.launch {
        sendEmailVerificationResponse = Loading
        sendEmailVerificationResponse = repo.sendEmailVerification()
    }
}