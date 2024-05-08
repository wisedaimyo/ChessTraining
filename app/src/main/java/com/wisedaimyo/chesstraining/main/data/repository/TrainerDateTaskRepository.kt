package com.wisedaimyo.chesstraining.main.data.repository

import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerDateTask

typealias CreateNewTrainerDateTaskResponse = Response<String>
typealias RemoveTrainerDateTaskWithIdResponse = Response<Boolean>
typealias AddStudentToTheTaskWithIdResponse = Response<Boolean>
typealias GetAllTrainerDateTasksForTrainerTasks = Response<List<Pair<String, TrainerDateTask>>>
typealias GetTrainerDateTaskWithIdResponse = Response<TrainerChessTask>

interface TrainerDateTaskRepository {
    suspend fun createNewTrainerDateTask(dateTask: TrainerDateTask): CreateNewTrainerDateTaskResponse
    suspend fun getTrainerDateTaskWithId(taskId: String): GetTrainerDateTaskWithIdResponse
    suspend fun addStudentToTheTaskWithId(taskId: String, studentId: String): AddStudentToTheTaskWithIdResponse
    suspend fun removeTrainerDateTaskWithId(taskId: String): RemoveTrainerDateTaskWithIdResponse
    suspend fun getAllDateTasks(dateTaskList: List<String>): GetAllTrainerDateTasksForTrainerTasks
}