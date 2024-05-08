package com.wisedaimyo.chesstraining

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.UseCaseUsers
import com.wisedaimyo.chesstraining.main.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val useCaseUsers: UseCaseUsers
): ViewModel() {

    init {
        getAuthState()
    }

    var currentUser by mutableStateOf(User())

    fun getAuthState() = repo.getAuthState(viewModelScope)

    val isEmailVerified get() = repo.currentUser?.isEmailVerified ?: false

    fun getCurrentUser(userId: String) = viewModelScope.launch {
        when(val response = useCaseUsers.getCurrentUser(userId = userId)) {
            is Response.Success -> currentUser = response.data!!
            else -> {}
        }
    }

}