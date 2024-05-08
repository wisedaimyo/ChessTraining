package com.wisedaimyo.chesstraining.main.data.repository

import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask

typealias CreateNewTrainerChessTaskResponse = Response<String>
typealias AddStudentToDoneResponse = Response<Boolean>
typealias RemoveTrainerChessTaskResponse = Response<Boolean>
typealias UpdateTrainerChessTaskResponse = Response<Boolean>
typealias RemoveStudentToDoneResponse = Response<Boolean>
typealias CheckStudentToDoneResponse = Response<Boolean>
typealias GetAllTrainerChessForTrainerTasks = Response<List<Pair<String, TrainerChessTask>>>
typealias GetTrainerChessTaskWithIdResponse = Response<TrainerChessTask>

interface TrainerChessTaskRepository {
    suspend fun updateTrainerChessTask(chessTask: TrainerChessTask, documentId: String): UpdateTrainerChessTaskResponse
    suspend fun createNewTrainerChessTask(chessTask: TrainerChessTask): CreateNewTrainerChessTaskResponse
    suspend fun removeTrainerChessTask(documentId: String): RemoveTrainerChessTaskResponse
    suspend fun addStudentToDone(studentId: String, courseId: String): AddStudentToDoneResponse
    suspend fun removeStudentToDone(studentId: String, courseId: String): RemoveStudentToDoneResponse
    suspend fun checkIfStudentIsDone(studentId: String, courseId: String): CheckStudentToDoneResponse
    suspend fun getTrainerChessTaskWithId(taskId: String): GetTrainerChessTaskWithIdResponse
    suspend fun getAllTrainerTasks(taskList: List<String>): GetAllTrainerChessForTrainerTasks
}