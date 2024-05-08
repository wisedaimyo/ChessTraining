package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerCourse
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerDateTask
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.repository.SignOutResponse
import com.wisedaimyo.chesstraining.main.data.repository.TrainerChessTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerCourseRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerDateTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditorViewModel @Inject constructor(
    private val repo: UserRepository,
    private val repo2: TrainerCourseRepository,
    private val repo3: TrainerDateTaskRepository,
    private val repo4: TrainerChessTaskRepository
): ViewModel() {
    var currentUser by mutableStateOf(User(elo = 0))
        private set
    var isLoaded by mutableStateOf(false)
        private set
    var chessTasks by mutableStateOf(mutableListOf<Pair<String, TrainerChessTask>>())
        private set
    var openings by mutableStateOf(mutableListOf<Pair<String, TrainerChessTask>>())
        private set
    var middle by mutableStateOf(mutableListOf<Pair<String, TrainerChessTask>>())
        private set
    var end by mutableStateOf(mutableListOf<Pair<String, TrainerChessTask>>())
        private set
    var other by mutableStateOf(mutableListOf<Pair<String, TrainerChessTask>>())
        private set

    var dateTasks by mutableStateOf(mutableListOf<Pair<String, TrainerDateTask>>())
        private set

    init {
      //  getCurrentUser()
    }

    fun getCourseDataForTrainer() = viewModelScope.launch {
        when(val response = Firebase.auth.uid?.let { repo2.getTrainerCourseForTrainer(it) }){
            is Response.Success -> {
                val dateTasksList = response.data?.second?.trainerDateTask
                val chessTasksList = response.data?.second?.trainerChessTask
                isLoaded = true
                if(dateTasksList != null) {
                    getDateTasks(dateTasksList)
                }
                if(chessTasksList != null) {
                    getChessTasks(chessTasksList)
                }
            }
            else -> { }
        }
    }

    private fun getDateTasks(dateTaskList: List<String>) = viewModelScope.launch {
        when(val response = repo3.getAllDateTasks(dateTaskList)) {
            is Response.Success -> { dateTasks = response.data as MutableList<Pair<String, TrainerDateTask>>
            }
            else -> { }
        }
    }

    private fun getChessTasks(chessTaskList: List<String>) = viewModelScope.launch {
        when(val response = repo4.getAllTrainerTasks(chessTaskList)) {
            is Response.Success -> { chessTasks = response.data as MutableList<Pair<String, TrainerChessTask>>
            }
            else -> { }
        }
    }

    private fun getCurrentUser() = viewModelScope.launch {
        when(val response = Firebase.auth.uid?.let { repo.getCurrentUserFromFirestore(it) }) {
            is Response.Success -> {
                currentUser = response.data!!
            }
            else -> { }
        }
    }
}