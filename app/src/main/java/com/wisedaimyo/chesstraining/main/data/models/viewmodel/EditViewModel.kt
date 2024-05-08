package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.repository.GetTrainerChessTaskWithIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveTrainerChessTaskResponse
import com.wisedaimyo.chesstraining.main.data.repository.TrainerChessTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerCourseRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerDateTaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditViewModel @Inject
constructor(
    private val repo: TrainerChessTaskRepository
): ViewModel() {

    var chessTaskResponse by mutableStateOf<GetTrainerChessTaskWithIdResponse>(Response.Loading)
    var isRemovedResponse by mutableStateOf<RemoveTrainerChessTaskResponse>(Response.Loading)

    var isLoaded by mutableStateOf(false)


    fun getTrainerChessTask(chessTaskId: String) = viewModelScope.launch {
        chessTaskResponse = Response.Loading
        chessTaskResponse = repo.getTrainerChessTaskWithId(chessTaskId)
    }

    fun removeChessTask(chessTaskId: String) = viewModelScope.launch {
        isRemovedResponse = Response.Loading
        isRemovedResponse = repo.removeTrainerChessTask(chessTaskId)
    }


}