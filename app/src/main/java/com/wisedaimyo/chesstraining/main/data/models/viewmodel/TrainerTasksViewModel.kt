package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerDateTask
import com.wisedaimyo.chesstraining.main.data.repository.AddToTheTrainerCourseChessCourseTaskResponse
import com.wisedaimyo.chesstraining.main.data.repository.AddToTneTrainerCourseDateTaskResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetAllTrainerDateTasksForTrainerTasks
import com.wisedaimyo.chesstraining.main.data.repository.GetTrainerChessTaskWithIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.TrainerChessTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerCourseRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerDateTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.UpdateTrainerChessTaskResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainerTasksViewModel @Inject
constructor(
    private val repo: TrainerDateTaskRepository,
    private val repo3: TrainerChessTaskRepository,
    private val repo2: TrainerCourseRepository
): ViewModel() {
    var trainerDateTasksResponse by mutableStateOf<AddToTheTrainerCourseChessCourseTaskResponse>(Response.Loading)
        private set

    var createNewDateTaskResponse by mutableStateOf<AddToTneTrainerCourseDateTaskResponse>(Response.Loading)
    var chessTaskResponse by mutableStateOf<GetTrainerChessTaskWithIdResponse>(Response.Loading)

    var isUpdated by mutableStateOf<UpdateTrainerChessTaskResponse>(Response.Loading)

    var isFinished by mutableStateOf(false)

    fun updateTrainerChessTask(chessTask: TrainerChessTask, documentId: String) = viewModelScope.launch {
        isUpdated = Response.Loading
        isUpdated = repo3.updateTrainerChessTask(chessTask, documentId)
    }

    fun isFinishedChessTask(chessTaskId: String, studentID: String) = viewModelScope.launch {
        when(val response = repo3.checkIfStudentIsDone(studentID, chessTaskId)) {
            is Response.Success -> { isFinished = response.data == true }
            else -> { }
        }
    }

    fun setChessTaskFinished(chessTaskId: String, studentID: String) = viewModelScope.launch {
        when(val response = repo3.addStudentToDone(studentID, chessTaskId)) {
            is Response.Success -> { isFinishedChessTask(chessTaskId, studentID) }
            else -> { isFinished = false }
        }
    }

    fun removeChessTaskFinished(chessTaskId: String, studentID: String) = viewModelScope.launch {
        when(val response = repo3.removeStudentToDone(studentID, chessTaskId)) {
            is Response.Success -> { isFinishedChessTask(chessTaskId, studentID) }
            else -> { isFinished = false }
        }
    }


    fun getTrainerChessTask(chessTaskId: String) = viewModelScope.launch {
        chessTaskResponse = Response.Loading
        chessTaskResponse = repo3.getTrainerChessTaskWithId(chessTaskId)
    }

    fun createNewTrainerDateTask(trainerDateTask: TrainerDateTask, currentTrainerCourse: String) = viewModelScope.launch {
        when(val response = repo.createNewTrainerDateTask(trainerDateTask)) {
            is Response.Success -> {
                response.data?.let { addToTrainerCourseDateTask(currentTrainerCourse, it) }
            }
            else -> { }
        }
    }

    private fun addToTrainerCourseDateTask(currentTrainerCourse: String, idOfCreatedDateTask: String) = viewModelScope.launch {
        createNewDateTaskResponse = Response.Loading
        createNewDateTaskResponse = repo2.addToTheTrainerCourseDateTask(currentTrainerCourse, idOfCreatedDateTask)
    }

    fun createNewTrainerChessTask(trainerChessTask: TrainerChessTask, currentTrainerCourse: String) = viewModelScope.launch {
        when(val response = repo3.createNewTrainerChessTask(trainerChessTask)) {
            is Response.Success -> {
                response.data?.let { addToTrainerCourseChessTask(currentTrainerCourse, it) }
            }
            else -> { }
        }
    }

    private fun addToTrainerCourseChessTask(currentTrainerCourse: String, idOfCreatedChessTask: String) = viewModelScope.launch {
        trainerDateTasksResponse = Response.Loading
        trainerDateTasksResponse = repo2.addToTheTrainerCourseChessCourseTask(currentTrainerCourse, idOfCreatedChessTask)
    }



}