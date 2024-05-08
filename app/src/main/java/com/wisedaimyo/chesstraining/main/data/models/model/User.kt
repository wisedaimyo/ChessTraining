package com.wisedaimyo.chesstraining.main.data.models.model

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import java.util.Date

data class User(
    var createdAt: Timestamp? = Timestamp.now(),
    var email: String? = null,
    var displayName: String? = null,
    var photoUrl: String? = null,

    var invitations: List<String>? = null,
    var trainerCourse: String? = null,

    var isTrainer: Boolean? = null,
    var image: String? = null,
    var elo: Int? = null,
    var puzzleElo: Int? = null,
    var puzzleSolved: Int? = null,
    var puzzleStrike: Int? = null
)