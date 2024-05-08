package com.wisedaimyo.chesstraining.main.data.models.model

import com.google.firebase.Timestamp

data class TrainerDateTask(
    val description: String? = null,
    val name: String? = null,
    val createdBy: String? = null,
    val done: List<String>? = null,
    val finish: Timestamp? = null
)