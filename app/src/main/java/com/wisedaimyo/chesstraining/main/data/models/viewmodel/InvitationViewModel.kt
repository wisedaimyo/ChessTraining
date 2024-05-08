package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.repository.GetDocumentIdInvitationResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetInvitationForCurrentUser
import com.wisedaimyo.chesstraining.main.data.repository.InvitationRepository
import com.wisedaimyo.chesstraining.main.data.repository.InviteUserToGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.InviteUserWithNameToGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveDocumentToWhoResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveInvitationForIdResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val repo: InvitationRepository
): ViewModel() {
    var inviteUserToGameResponse by mutableStateOf<InviteUserToGameResponse>(Response.Loading)
        private set
    var inviteUserWithNameToGameResponse by mutableStateOf<InviteUserWithNameToGameResponse>(Response.Loading)
        private set
    var removeInvitationResponse by mutableStateOf<RemoveInvitationForIdResponse>(Response.Loading)
        private set
    var removeDocumentToWhoResponse by mutableStateOf<RemoveDocumentToWhoResponse>(Response.Loading)
        private set
    var getInvitationId by mutableStateOf<GetDocumentIdInvitationResponse>(Response.Loading)
        private set
    var getAllInvitations by mutableStateOf<GetInvitationForCurrentUser>(Response.Loading)
        private set


    fun removeGivenInvitation(toWhoId: String, whoUserId: String, time: Int) = viewModelScope.launch {
        removeDocumentToWhoResponse = Response.Loading
        removeDocumentToWhoResponse = repo.removeDocumentToWhoWhoUser(toWhoId, whoUserId, time)
    }

    fun getInvitationId(toWhoId: String, whoUserId: String, time: Int) = viewModelScope.launch {
        getInvitationId = Response.Loading
        getInvitationId = repo.getDocumentIdInvitation(toWhoId, whoUserId)
    }

    fun removeInvitation(invitationId: String) = viewModelScope.launch {
        removeInvitationResponse = Response.Loading
        removeInvitationResponse = repo.removeInvitationForId(invitationId)
    }

    fun getAllInvitations(userId: String) = viewModelScope.launch {
        getAllInvitations = Response.Loading
        getAllInvitations = repo.getInvitationsForCurrentUser(userId)
    }

    fun inviteUserWithNameToGame(userName: String, time: Int) = viewModelScope.launch {
        inviteUserWithNameToGameResponse = Response.Loading
        inviteUserWithNameToGameResponse = repo.inviteUserWithNameToGame(userName, time)
    }

}