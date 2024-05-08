package com.wisedaimyo.chesstraining.main.data

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.wisedaimyo.chesstraining.Constants
import com.wisedaimyo.chesstraining.Constants.INVITATION
import com.wisedaimyo.chesstraining.Constants.USERS
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.ChessCourse
import com.wisedaimyo.chesstraining.main.data.models.model.Invitation
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.repository.GetDocumentIdInvitationResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetInvitationForCurrentUser
import com.wisedaimyo.chesstraining.main.data.repository.InvitationRepository
import com.wisedaimyo.chesstraining.main.data.repository.InviteUserToGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.InviteUserWithNameToGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveDocumentToWhoResponse
import com.wisedaimyo.chesstraining.main.data.repository.RemoveInvitationForIdResponse
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class InvitationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : InvitationRepository {
    override suspend fun removeDocumentToWhoWhoUser(
        toWhoId: String,
        whoUserId: String,
        time: Int,
    ): RemoveDocumentToWhoResponse {

        return try {
            val querySnapshot = fireStore.collection(INVITATION)
                .whereEqualTo("toUserId", toWhoId)
                .whereEqualTo("whoUserId", whoUserId)
                .whereEqualTo("time", time)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents[0].id
                fireStore.collection(INVITATION).document(documentId).delete().await()
                Response.Success(true)
            } else {
                Response.Failure(Exception("User not found"))
            }

            Response.Success(false)

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getDocumentIdInvitation(
        toWhoId: String,
        whoUserId: String
    ): GetDocumentIdInvitationResponse {
        try {
            val querySnapshot = fireStore.collection(INVITATION)
                .whereEqualTo("toUserId", toWhoId)
                .whereEqualTo("fromUserId", whoUserId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents.first().id
                return Response.Success(documentId)
            } else {
                return Response.Failure(Exception("Invitation not found"))
            }

        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }


    override suspend fun removeInvitationForId(invitationId: String): RemoveInvitationForIdResponse {
        try {
            fireStore.collection(INVITATION)
                .document(invitationId)
                .delete()
                .await()

            return Response.Success(true)
        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }



    override suspend fun getInvitationsForCurrentUser(userId: String): GetInvitationForCurrentUser {
        val invitations: MutableList<Invitation> = mutableListOf()

        return try {
            val querySnapshot = fireStore.collection(INVITATION)
                .whereEqualTo("toUserId", userId)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                val invitation = document.toObject(Invitation::class.java)
                if (invitation != null) {
                    invitations.add(invitation)
                }
            }

            Response.Success(invitations)

        } catch(e: Exception) {
            Response.Failure(e)
        }

    }
    override suspend fun inviteUserToGame(elo: Int, time: Int, currentUserName: String): InviteUserToGameResponse {
        var userId: String? = null
        return try {
            fireStore.collection(USERS)
                .whereGreaterThanOrEqualTo("elo", elo-450)
                .whereLessThanOrEqualTo("elo", elo+450)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents?.filter { it.getString("displayName") != currentUserName }
                        if (!documents.isNullOrEmpty()) {
                            val randomIndex = (0 until documents.size).random()
                            val randomDocument = documents[randomIndex]
                            userId = randomDocument.id
                            println("Random User ID in the specified age range: $userId")
                        } else {
                            println("No users found in the collection within the given age range.")
                        }
                    } else {
                        println("Error fetching users: ${task.exception?.message}")
                    }

                    if(userId!=null) {
                        val invitation = Invitation(
                            time = time,
                            whoUserId = auth.uid,
                            toUserId = userId
                        )
                        fireStore.collection(INVITATION).add(invitation)

                    }

                }.await()
            Response.Success(true)

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun inviteUserWithNameToGame(userName: String, time: Int): InviteUserWithNameToGameResponse {
        var userId: String? = null

        return try {
            fireStore.collection(USERS)
                .whereEqualTo("displayName", userName)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result?.documents
                        if (!documents.isNullOrEmpty()) {
                            val document = documents.first()
                            userId = document.id
                        } else {
                            println("No users found in the collection within the given age range.")
                        }
                    } else {
                        println("Error fetching users: ${task.exception?.message}")
                    }

                    if(userId!=null) {
                        val invitation = Invitation(
                            time = time,
                            whoUserId = auth.uid,
                            toUserId = userId
                        )
                        println("INVITAITON $invitation")
                        fireStore.collection(INVITATION).add(invitation)
                    } else {
                        Response.Success(false)
                    }
                }.await()

            Response.Success(true)

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }


}