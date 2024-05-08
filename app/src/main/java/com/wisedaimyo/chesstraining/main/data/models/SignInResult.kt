package com.wisedaimyo.chesstraining.main.data.models

data class SignInResult(
    val data: com.wisedaimyo.chesstraining.main.data.models.UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)