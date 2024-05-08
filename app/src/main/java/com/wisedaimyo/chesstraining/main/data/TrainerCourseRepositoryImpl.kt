package com.wisedaimyo.chesstraining.main.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.wisedaimyo.chesstraining.Constants
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerCourse
import com.wisedaimyo.chesstraining.main.data.repository.AcceptInvitationResponse
import com.wisedaimyo.chesstraining.main.data.repository.AddTheStudentInToCourseResponse
import com.wisedaimyo.chesstraining.main.data.repository.AddToTheTrainerCourseChessCourseTaskResponse
import com.wisedaimyo.chesstraining.main.data.repository.AddToTneTrainerCourseDateTaskResponse
import com.wisedaimyo.chesstraining.main.data.repository.CreateNewTrainerCourseResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetAllChessTasksResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetAllTimedTasksResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetCourseIDForStudent
import com.wisedaimyo.chesstraining.main.data.repository.GetInvitationResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetTrainerCourseForStudentResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetTrainerCourseForTrainerResponse
import com.wisedaimyo.chesstraining.main.data.repository.SignOutFromCourseResponse
import com.wisedaimyo.chesstraining.main.data.repository.TrainerCourseRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainerCourseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : TrainerCourseRepository {

    override suspend fun inviteStudentToTheCourse(trainerId: String, studentId: String): AddTheStudentInToCourseResponse {
        return try {
            val querySnapshot = fireStore
                .collection(Constants.TRAINER_COURSE)
                .whereEqualTo("trainerId", trainerId)
                .get()
                .await()

            val courseDocument = querySnapshot.documents.firstOrNull()
            if (courseDocument != null) {
                val studentsList = courseDocument.get("invited") as? MutableList<String>
                if (studentsList != null) {
                    if(!studentsList.contains(studentId))
                        studentsList.add(studentId)
                    else
                        Response.Success(false)

                    courseDocument.reference.update("invited", studentsList).await()
                    Response.Success(true)
                } else {
                    Response.Failure(Exception("Students list not found"))
                }
            } else {
                Response.Failure(Exception("Course not found"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun addToTheTrainerCourseDateTask(
        currentCourseId: String,
        dateTaskId: String,
    ): AddToTneTrainerCourseDateTaskResponse {
        return try {
            val courseDocument = fireStore
                .collection(Constants.TRAINER_COURSE)
                .document(currentCourseId)
                .get()
                .await()

            if (courseDocument != null) {
                val trainerDateTasks = courseDocument.get("trainerDateTask") as? MutableList<String>
                if (trainerDateTasks != null) {
                    if(!trainerDateTasks.contains(dateTaskId))
                        trainerDateTasks.add(dateTaskId)
                    else
                        Response.Success(false)

                    courseDocument.reference.update("trainerDateTask", trainerDateTasks).await()
                    Response.Success(true)
                } else {
                    Response.Failure(Exception("Students list not found"))
                }
            } else {
                Response.Failure(Exception("Course not found"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun addToTheTrainerCourseChessCourseTask(
        currentCourseId: String,
        chessTaskId: String,
    ): AddToTheTrainerCourseChessCourseTaskResponse {
        return try {
            val courseDocument = fireStore
                .collection(Constants.TRAINER_COURSE)
                .document(currentCourseId)
                .get()
                .await()

            if (courseDocument != null) {
                val chessTaskList = courseDocument.get("trainerChessTask") as? MutableList<String>
                if (chessTaskList != null) {
                    if(!chessTaskList.contains(chessTaskId))
                        chessTaskList.add(chessTaskId)
                    else
                        Response.Success(false)

                    courseDocument.reference.update("trainerChessTask", chessTaskList).await()
                    Response.Success(true)
                } else {
                    Response.Failure(Exception("Tasks list not found"))
                }
            } else {
                Response.Failure(Exception("Course not found"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getInvitationsFromTrainerCourse(studentId: String): GetInvitationResponse {
        return try {
            val querySnapshot = fireStore.collection(Constants.TRAINER_COURSE)
                .whereArrayContains("invited", studentId)
                .get()
                .await()

            val invitations = querySnapshot.documents.mapNotNull { document ->
                val trainerCourse = document.toObject(TrainerCourse::class.java)
                val trainerCourseId = document.id
                if (trainerCourse != null) {
                    Pair(trainerCourseId, trainerCourse)
                } else {
                    null
                }
            }

            if (invitations.isNotEmpty()) {
                Response.Success(invitations)
            } else {
                Response.Failure(Exception("No invitations found for the student"))
            }
        } catch(e: Exception) {
            Response.Failure(e)
        }
    }



    override suspend fun getTrainerCourseForTrainer(trainerId: String): GetTrainerCourseForTrainerResponse {
        return try {
            val querySnapshot = fireStore.collection(Constants.TRAINER_COURSE)
                .whereEqualTo("trainerId", auth.uid)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                val trainerCourse = document.toObject(TrainerCourse::class.java)
                val trainerCourseId = document.id
                if (trainerCourse != null) {
                    return Response.Success(Pair(trainerCourseId, trainerCourse))
                }
            }

             Response.Failure(Exception("Nothing found"))
        } catch(e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getTrainerCourseForStudent(studentId: String): GetTrainerCourseForStudentResponse {
        return try {
            val querySnapshot = fireStore.collection(Constants.TRAINER_COURSE)
                .whereArrayContains("students", studentId)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                val trainerCourse = document.toObject(TrainerCourse::class.java)
                val trainerCourseId = document.id
                if (trainerCourse != null) {
                    return Response.Success(Pair(trainerCourseId, trainerCourse))
                }
            }

            Response.Failure(Exception("Nothing found"))
        } catch(e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun signOutFromCourse(studentId: String): SignOutFromCourseResponse {
        return try {
            val querySnapshot = fireStore.collection(Constants.TRAINER_COURSE)
                .whereArrayContains("students", studentId)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                return Response.Failure(Exception("Student not found in any course"))
            }

            for (document in querySnapshot.documents) {
                val trainerCourse = document.toObject(TrainerCourse::class.java)
                val studentsMutableList = trainerCourse?.students?.toMutableList()
                studentsMutableList?.remove(studentId)
                trainerCourse?.students = studentsMutableList
                fireStore.collection(Constants.TRAINER_COURSE).document(document.id)
                    .set(trainerCourse!!)
                    .await()
            }

            Response.Success(true)
        } catch(e: Exception) {
            Response.Failure(e)
        }
    }



    override suspend fun getCourseIDForStudent(studentId: String): GetCourseIDForStudent {
        TODO("Not yet implemented")
    }

    override suspend fun getCourseIDForTrainer(trainerId: String): GetCourseIDForStudent {
        TODO("Not yet implemented")
    }

    override suspend fun getAllChessTasks(courseId: String): GetAllChessTasksResponse {
        TODO("Not yet implemented")
    }



    override suspend fun getAllDateTasks(courseId: String): GetAllTimedTasksResponse {
        TODO("Not yet implemented")
    }

    override suspend fun acceptInvitation(courseId: String, studentId: String): AcceptInvitationResponse {
        return try {
            val docRef = fireStore.collection(Constants.TRAINER_COURSE).document(courseId)
            docRef.update("invited", FieldValue.arrayRemove(studentId)).await()
            docRef.update("students", FieldValue.arrayUnion(studentId)).await()
            Response.Success(true)
        } catch(e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun createNewTrainerCourse(trainerCourse: TrainerCourse): CreateNewTrainerCourseResponse {
        return try {
            fireStore
                .collection(Constants.TRAINER_COURSE)
                .add(trainerCourse)
                .await()

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }


}
