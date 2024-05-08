package com.wisedaimyo.chesstraining.main.data


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wisedaimyo.chesstraining.Constants.CHESS_COURSE
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.ChessCourse
import com.wisedaimyo.chesstraining.main.data.repository.ChessCourseRepository
import com.wisedaimyo.chesstraining.main.data.repository.GetCourseWithNameResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetCoursesResponse
import com.wisedaimyo.chesstraining.main.data.repository.SignOutResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChessCourseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ChessCourseRepository {

    override suspend fun getCourseWithNameFromFirestore(courseName: String): GetCourseWithNameResponse {
        return try {
            val document = firestore.collection(CHESS_COURSE)
                .document(courseName)
                .get()
                .await()

                val course = document.toObject(ChessCourse::class.java)
                Response.Success(course)

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }


    override suspend fun getAllCoursesFromFirestore(): GetCoursesResponse {
        return try {
            val querySnapshot = firestore.collection(CHESS_COURSE)
                .get()
                .await()

            val courses = querySnapshot.documents.mapNotNull { document ->
                document.toObject(ChessCourse::class.java)
            }

            Response.Success(courses)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }
}
