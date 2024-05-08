package com.wisedaimyo.chesstraining.main.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.wisedaimyo.chesstraining.Constants
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask
import com.wisedaimyo.chesstraining.main.data.repository.AddStudentToDoneResponse
import com.wisedaimyo.chesstraining.main.data.repository.CheckStudentToDoneResponse
import com.wisedaimyo.chesstraining.main.data.repository.CreateNewTrainerChessTaskResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetAllTrainerChessForTrainerTasks
import com.wisedaimyo.chesstraining.main.data.repository.GetTrainerChessTaskWithIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveStudentToDoneResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveTrainerChessTaskResponse
import com.wisedaimyo.chesstraining.main.data.repository.TrainerChessTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.UpdateTrainerChessTaskResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainerChessTaskRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
): TrainerChessTaskRepository {

    override suspend fun updateTrainerChessTask(chessTask: TrainerChessTask, documentId: String): UpdateTrainerChessTaskResponse {
        return try {
            db.collection(Constants.TRAINER_CHESS_TASK)
                .document(documentId)
                .set(chessTask)
                .await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Success(false)
        }
    }

    override suspend fun removeTrainerChessTask(documentId: String): RemoveTrainerChessTaskResponse {
        return try {
            db.collection(Constants.TRAINER_CHESS_TASK)
                .document(documentId)
                .delete()
                .await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Success(false)
        }
    }


    override suspend fun createNewTrainerChessTask(chessTask: TrainerChessTask): CreateNewTrainerChessTaskResponse {
        return try {
            val documentReference = db
                .collection(Constants.TRAINER_CHESS_TASK)
                .add(chessTask)
                .await()

            val documentId = documentReference.id
            Response.Success(documentId)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun addStudentToDone(
        studentId: String,
        courseId: String
    ): AddStudentToDoneResponse {
        return try {
            val docRef = db.collection(Constants.TRAINER_CHESS_TASK).document(courseId)
            docRef.update("done", FieldValue.arrayUnion(studentId)).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun removeStudentToDone(
        studentId: String,
        courseId: String
    ): RemoveStudentToDoneResponse {
        return try {
            val docRef = db.collection(Constants.TRAINER_CHESS_TASK).document(courseId)
            docRef.update("done", FieldValue.arrayRemove(studentId)).await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun checkIfStudentIsDone(
        studentId: String,
        courseId: String
    ): CheckStudentToDoneResponse {
        return try {
            val docRef = db.collection(Constants.TRAINER_CHESS_TASK).document(courseId)
            val snapshot = docRef.get().await()
            val doneList = snapshot.get("done") as List<*>
            val isDone = studentId in doneList
            Response.Success(isDone)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }


    override suspend fun getAllTrainerTasks(taskList: List<String>): GetAllTrainerChessForTrainerTasks {
        return try {
            val tasks = mutableListOf<Pair<String, TrainerChessTask>>()
            for (taskId in taskList) {
                val documentSnapshot = db
                    .collection(Constants.TRAINER_CHESS_TASK)
                    .document(taskId)
                    .get()
                    .await()

                documentSnapshot.toObject(TrainerChessTask::class.java)?.let { tasks.add(Pair(taskId, it)) }
            }
            Response.Success(tasks)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }


    override suspend fun getTrainerChessTaskWithId(taskId: String): GetTrainerChessTaskWithIdResponse {
        return try {
            val documentSnapshot = db
                .collection(Constants.TRAINER_CHESS_TASK)
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



}