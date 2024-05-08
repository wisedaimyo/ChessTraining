package com.wisedaimyo.chesstraining.main.data.repository

import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.ChessCourse

typealias GetCourseWithNameResponse = Response<ChessCourse>
typealias GetCoursesResponse = Response<List<ChessCourse>>

interface ChessCourseRepository {
    suspend fun getCourseWithNameFromFirestore(courseName: String): GetCourseWithNameResponse
    suspend fun getAllCoursesFromFirestore(): GetCoursesResponse
}

