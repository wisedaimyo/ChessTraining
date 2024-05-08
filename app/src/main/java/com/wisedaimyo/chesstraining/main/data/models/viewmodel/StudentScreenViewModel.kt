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
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.UseCaseUsers
import com.wisedaimyo.chesstraining.main.data.repository.AddStudentToTheTaskWithIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.AuthRepository
import com.wisedaimyo.chesstraining.main.data.repository.GetInvitationResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetTrainerCourseForTrainerResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveTrainerDateTaskWithIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.SignOutResponse
import com.wisedaimyo.chesstraining.main.data.repository.TrainerChessTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerCourseRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerDateTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentScreenViewModel @Inject constructor(
    private val repo: UserRepository,
    private val repo2: TrainerCourseRepository,
    private val repo3: TrainerDateTaskRepository,
    private val repo4: TrainerChessTaskRepository
): ViewModel() {

    var currentUser by mutableStateOf(User(elo = 0))
        private set

    var isLoaded by mutableStateOf(false)
        private set

    var chessTasks by mutableStateOf(listOf<Pair<String, TrainerChessTask>>())
    var openings by mutableStateOf(listOf<Pair<String, TrainerChessTask>>())
    var middle by mutableStateOf(listOf<Pair<String, TrainerChessTask>>())
    var end by mutableStateOf(listOf<Pair<String, TrainerChessTask>>())
    var other by mutableStateOf(listOf<Pair<String, TrainerChessTask>>())

    var removeTrainerDateTaskWithIdResponse by mutableStateOf<RemoveTrainerDateTaskWithIdResponse>(Response.Loading)
        private set

    var addStudentToTheTaskWithIdResponse by mutableStateOf<AddStudentToTheTaskWithIdResponse>(Response.Loading)

    var signOutResponse by mutableStateOf<SignOutResponse>(Response.Loading)
        private set

    var dateTasks by mutableStateOf(mutableListOf<Pair<String, TrainerDateTask>>())
        private set

    var invitations by mutableStateOf(mutableListOf<Pair<String, TrainerCourse>>())
        private set


    init {
        getCurrentUser()
    }

    fun acceptInvitation(courseId: String, studentId: String) = viewModelScope.launch {
        when(val response = repo2.acceptInvitation(courseId, studentId)) {
            is Response.Success -> { getCourseDataForStudent() }
            else -> { }
        }
    }

    fun getInvitationsForStudent(studentId: String) = viewModelScope.launch {
        when(val response = repo2.getInvitationsFromTrainerCourse(studentId)) {
            is Response.Success -> { invitations = response.data as MutableList<Pair<String, TrainerCourse>>
            }
            else -> {}
        }
    }

    fun signOutStudent(studentId: String) = viewModelScope.launch {
        signOutResponse = Response.Loading
        signOutResponse = repo2.signOutFromCourse(studentId)
    }

    fun getCourseDataForStudent() = viewModelScope.launch {
        when(val response = Firebase.auth.uid?.let { repo2.getTrainerCourseForStudent(it) }) {
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

    fun removeDateTask(taskId: String) = viewModelScope.launch {
        removeTrainerDateTaskWithIdResponse = Response.Loading
        removeTrainerDateTaskWithIdResponse = repo3.removeTrainerDateTaskWithId(taskId)
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

    fun addStudentToTheDateTask(studentId: String, dateTaskId: String) = viewModelScope.launch {
        addStudentToTheTaskWithIdResponse = Response.Loading
        addStudentToTheTaskWithIdResponse = repo3.addStudentToTheTaskWithId(studentId = studentId, taskId = dateTaskId)
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
                if(!currentUser.isTrainer!!) getCourseDataForStudent()
            }
            else -> { }
        }
    }
}