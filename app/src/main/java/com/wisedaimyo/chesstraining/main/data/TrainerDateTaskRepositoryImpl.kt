package com.wisedaimyo.chesstraining.main.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.wisedaimyo.chesstraining.Constants
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.PlayedGame
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerDateTask
import com.wisedaimyo.chesstraining.main.data.repository.AddStudentToTheTaskWithIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.AuthRepository
import com.wisedaimyo.chesstraining.main.data.repository.CreateNewTrainerDateTaskResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetAllTrainerDateTasksForTrainerTasks
import com.wisedaimyo.chesstraining.main.data.repository.GetTrainerDateTaskWithIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveTrainerDateTaskWithIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.TrainerDateTaskRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainerDateTaskRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : TrainerDateTaskRepository {

    override suspend fun createNewTrainerDateTask(dateTask: TrainerDateTask): CreateNewTrainerDateTaskResponse  {
        return try {
            val documentReference = firestore
                .collection(Constants.TRAINER_DATE_TASK)
                .add(dateTask)
                .await()
            val documentId = documentReference.id
            Response.Success(documentId)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun addStudentToTheTaskWithId(
        taskId: String,
        studentId: String,
    ): AddStudentToTheTaskWithIdResponse {
        return try {
            val documentReference = firestore
                .collection(Constants.TRAINER_DATE_TASK)
                .document(taskId)

            documentReference.update("done", FieldValue.arrayUnion(studentId)).await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getTrainerDateTaskWithId(taskId: String): GetTrainerDateTaskWithIdResponse {
        return try {
            val documentSnapshot = firestore
                .collection(Constants.TRAINER_DATE_TASK)
                .document(taskId)
                .get()
                .await()

            val task = documentSnapshot.toObject(TrainerChessTask::class.java)
            if (task != null) {
                Response.Success(task)
            } else {
                Response.Failure(Exception("Task not found"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }



    override suspend fun removeTrainerDateTaskWithId(taskId: String): RemoveTrainerDateTaskWithIdResponse {
        return try {
            firestore
                .collection(Constants.TRAINER_DATE_TASK)
                .document(taskId)
                .delete()
                .await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }



    override suspend fun getAllDateTasks(dateTaskList: List<String>): GetAllTrainerDateTasksForTrainerTasks {
        return try {
            val tasks = mutableListOf<Pair<String, TrainerDateTask>>()
            for (taskId in dateTaskList) {
                val documentSnapshot = firestore
                    .collection(Constants.TRAINER_DATE_TASK)
                    .document(taskId)
                    .get()
                    .await()

                documentSnapshot.toObject(TrainerDateTask::class.java)?.let { tasks.add(Pair(taskId, it)) }
            }
            Response.Success(tasks)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }




}