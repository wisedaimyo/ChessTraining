package com.wisedaimyo.chesstraining.main.data.repository

import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.ChessCourse
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerCourse
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerDateTask

typealias GetTrainerCourseForTrainerResponse = Response<Pair<String, TrainerCourse>>
typealias GetTrainerCourseForStudentResponse = Response<Pair<String, TrainerCourse>>
typealias AddToTneTrainerCourseDateTaskResponse = Response<Boolean>
typealias GetCourseIDForStudent = Response<String>
typealias GetCourseIDForTrainer = Response<String>
typealias AddToTheTrainerCourseChessCourseTaskResponse = Response<Boolean>
typealias GetInvitationResponse = Response<List<Pair<String, TrainerCourse>>>
typealias GetAllChessTasksResponse = Response<List<TrainerChessTask>>
typealias GetAllTimedTasksResponse = Response<List<TrainerDateTask>>
typealias CreateNewTrainerCourseResponse = Response<Boolean>
typealias AcceptInvitationResponse = Response<Boolean>
typealias SignOutFromCourseResponse = Response<Boolean>
typealias AddTheStudentInToCourseResponse = Response<Boolean>

interface TrainerCourseRepository {
    // Trainer only one course though possible to have more in db.
    suspend fun inviteStudentToTheCourse(trainerId: String, studentId: String): AddTheStudentInToCourseResponse

    suspend fun addToTheTrainerCourseDateTask(currentCourseId: String, dateTaskId: String): AddToTneTrainerCourseDateTaskResponse

    suspend fun addToTheTrainerCourseChessCourseTask(currentCourseId: String, chessTaskId: String): AddToTheTrainerCourseChessCourseTaskResponse

    suspend fun getTrainerCourseForTrainer(trainerId: String): GetTrainerCourseForTrainerResponse

    suspend fun getTrainerCourseForStudent(studentId: String): GetTrainerCourseForStudentResponse

    suspend fun getInvitationsFromTrainerCourse(studentId: String): GetInvitationResponse

    suspend fun getCourseIDForStudent(studentId: String): GetCourseIDForStudent

    suspend fun getCourseIDForTrainer(trainerId: String): GetCourseIDForStudent

    suspend fun getAllChessTasks(courseId: String): GetAllChessTasksResponse

    suspend fun signOutFromCourse(studentId: String): SignOutFromCourseResponse

    suspend fun getAllDateTasks(courseId: String): GetAllTimedTasksResponse

    suspend fun acceptInvitation(courseId: String, studentId: String): AcceptInvitationResponse

    suspend fun createNewTrainerCourse(trainerCourse: TrainerCourse): CreateNewTrainerCourseResponse
}