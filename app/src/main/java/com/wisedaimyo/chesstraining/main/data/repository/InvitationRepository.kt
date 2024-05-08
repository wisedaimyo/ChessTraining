package com.wisedaimyo.chesstraining.main.data.repository

import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.Invitation

typealias InviteUserToGameResponse = Response<Boolean>
typealias RemoveInvitationForIdResponse = Response<Boolean>
typealias InviteUserWithNameToGameResponse = Response<Boolean>
typealias RemoveDocumentToWhoResponse = Response<Boolean>
typealias GetDocumentIdInvitationResponse = Response<String>
typealias GetInvitationForCurrentUser = Response<List<Invitation>>

interface InvitationRepository {
    suspend fun removeDocumentToWhoWhoUser(toWhoId: String, whoUserId: String, time: Int): RemoveDocumentToWhoResponse
    suspend fun getDocumentIdInvitation(toWhoId: String, whoUserId: String): GetDocumentIdInvitationResponse
    suspend fun removeInvitationForId(invitationId: String): RemoveInvitationForIdResponse
    suspend fun getInvitationsForCurrentUser(userId: String): GetInvitationForCurrentUser
    suspend fun inviteUserToGame(elo: Int, time: Int, currentUserName: String): InviteUserToGameResponse
    suspend fun inviteUserWithNameToGame(userName: String, time: Int): InviteUserWithNameToGameResponse
}