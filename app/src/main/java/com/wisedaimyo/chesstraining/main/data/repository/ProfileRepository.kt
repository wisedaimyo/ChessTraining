package com.wisedaimyo.chesstraining.main.data.repository

import com.wisedaimyo.chesstraining.main.data.models.Response

typealias SignOutResponse = Response<Boolean>
typealias MyRevokeAccessResponse = Response<Boolean>

interface ProfileRepository {
    val displayName: String
    val photoUrl: String

    suspend fun signOut(): SignOutResponse

    suspend fun revokeAccess(): MyRevokeAccessResponse
}