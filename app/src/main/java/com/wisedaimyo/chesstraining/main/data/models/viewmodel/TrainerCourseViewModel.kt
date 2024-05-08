package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerCourse
import com.wisedaimyo.chesstraining.main.data.repository.AddTheStudentInToCourseResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetTrainerCourseForTrainerResponse
import com.wisedaimyo.chesstraining.main.data.repository.TrainerCourseRepository
import com.wisedaimyo.chesstraining.main.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainerCourseViewModel @Inject constructor(
    private val repo: TrainerCourseRepository,
    private val repo2: UserRepository
): ViewModel() {

    var trainerCourseResponse by mutableStateOf<GetTrainerCourseForTrainerResponse>(Response.Loading)
        private set
    var studentAddedResponse by mutableStateOf<AddTheStudentInToCourseResponse>(Response.Loading)


    var trainerCourseId by mutableStateOf(mutableStateOf(""))
    var trainerCourse by mutableStateOf(mutableStateOf(TrainerCourse()))

    init {
        Firebase.auth.uid?.let { getTrainerCourse(it) }
    }

    fun acceptInvitation(courseId: String, studentId: String) = viewModelScope.launch {
        when(val response = repo.acceptInvitation(courseId, studentId)) {
            is Response.Success -> {  }
            else -> { }
        }
    }

    fun addStudentToCourse(trainerId: String, studentName: String) = viewModelScope.launch {
        studentAddedResponse = Response.Loading
        when(val response = repo2.getUsersId(studentName)) {
            is Response.Success -> {
                studentAddedResponse = response.data?.let {
                    repo.inviteStudentToTheCourse(trainerId,
                        it
                    )
                }!!
            }
            else -> { }
        }
    }

    fun getTrainerCourse(trainerId: String) = viewModelScope.launch {
        trainerCourseResponse = Response.Loading
        trainerCourseResponse = repo.getTrainerCourseForTrainer(trainerId)
        when(trainerCourseResponse) {
            is Response.Success -> {
                trainerCourseId.value = (trainerCourseResponse as Response.Success<Pair<String, TrainerCourse>>).data?.first ?: ""
                trainerCourse.value = (trainerCourseResponse as Response.Success<Pair<String, TrainerCourse>>).data?.second!!
            }
            else -> { }
        }
    }

    fun createNewCourse(trainerCourse: TrainerCourse) = viewModelScope.launch {
        when(val response = repo.createNewTrainerCourse(trainerCourse)){
            is Response.Success -> {
                Firebase.auth.uid?.let { getTrainerCourse(it) }
            }
            else -> {}
        }
    }

}
