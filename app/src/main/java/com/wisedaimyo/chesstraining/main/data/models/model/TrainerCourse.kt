package com.wisedaimyo.chesstraining.main.data.models.model

data class TrainerCourse(
    val name: String? = null,
    val trainerId: String? = null,
    val description: String? = null,
    val invited: List<String>? = null,
    val trainerChessTask: List<String>? = null,
    val trainerDateTask: List<String>? = null,
    var students: List<String>? = null,
)
