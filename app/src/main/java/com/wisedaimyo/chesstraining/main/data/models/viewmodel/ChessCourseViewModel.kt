package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.repository.ChessCourseRepository
import com.wisedaimyo.chesstraining.main.data.repository.GetCourseWithNameResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetCoursesResponse
import com.wisedaimyo.chesstraining.main.data.repository.SignOutResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChessCourseViewModel @Inject constructor(
    private val repo: ChessCourseRepository
): ViewModel() {
    var getCourseResponse by mutableStateOf<GetCourseWithNameResponse>(Response.Loading)
        private set
    var getAllCoursesResponse by mutableStateOf<GetCoursesResponse>(Response.Loading)
        private set

    init {
        getAllCourses()
    }

    fun getCourseWithName(courseName: String) = viewModelScope.launch {
        getCourseResponse = Response.Loading
        getCourseResponse = repo.getCourseWithNameFromFirestore(courseName)
    }

    private fun getAllCourses() = viewModelScope.launch {
        getAllCoursesResponse = Response.Loading
        getAllCoursesResponse = repo.getAllCoursesFromFirestore()
    }
}
