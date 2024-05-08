package com.wisedaimyo.chesstraining.main.data

import android.annotation.SuppressLint
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.wisedaimyo.chesstraining.Constants.ELO
import com.wisedaimyo.chesstraining.Constants.USERS
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.repository.AddUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.DeleteUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetCurrentUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetUserWithNameResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetUsersWithIdsResponse
import com.wisedaimyo.chesstraining.main.data.repository.ResetEloResponse
import com.wisedaimyo.chesstraining.main.data.repository.UpdateEloResponse
import com.wisedaimyo.chesstraining.main.data.repository.UpdateImageResponse
import com.wisedaimyo.chesstraining.main.data.repository.UpdateNameResponse
import com.wisedaimyo.chesstraining.main.data.repository.UserRepository
import com.wisedaimyo.chesstraining.main.data.repository.UsersIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.UsersResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference
): UserRepository  {



    override fun getUsersFromFirestore() = callbackFlow {
        val snapshotListener = usersRef.orderBy(ELO).addSnapshotListener { snapshot, e ->
            val userResponse = if (snapshot != null) {
                val users = snapshot.toObjects(User::class.java)
                Response.Success(users)
            } else {
                Response.Failure(e)
            }
            trySend(userResponse)
        }
        awaitClose{
            snapshotListener.remove()
        }
    }

    override suspend fun updateEloToUserToFirestore(userId: String, elo: Int): UpdateEloResponse = try {
        usersRef.document(userId).update("elo", elo).await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Failure(e)
    }

    override suspend fun getUsersId(userName: String): UsersIdResponse {
        return try {
            val documentSnapshot = usersRef.whereEqualTo("displayName", userName).get().await()
            if (!documentSnapshot.isEmpty) {
                val user = documentSnapshot.documents[0].id
                Response.Success(user)
            } else {
                Response.Failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun updateImage(userId: String, image: String): UpdateImageResponse = try {
        usersRef.document(userId).update("image", image).await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Failure(e)
    }

    override suspend fun resetElo(userId: String): ResetEloResponse = try {
        usersRef.document(userId).update("puzzleElo", 1200).await()
        usersRef.document(userId).update("puzzleSolved", 0).await()
        usersRef.document(userId).update("puzzleStrike", 0).await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Failure(e)
    }

    override suspend fun updateName(userId: String, name: String): UpdateNameResponse = try {
        usersRef.document(userId).update("displayName", name).await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Failure(e)
    }

    override suspend fun getCurrentUserFromFirestore(userId: String): GetCurrentUserResponse {
        return try {
            val documentSnapshot = usersRef.document(userId).get().await()
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                Response.Success(user)
            } else {
                Response.Failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun addNewUserToFirestore(user: User, userId: String) = try {
        usersRef.document(userId).set(user).await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Failure(e)
    }

    override suspend fun getUserWithNameFromFirestore(userName: String): GetUserWithNameResponse {
        return try {
            val querySnapshot = usersRef.whereEqualTo("displayName", userName).get().await()
            if (!querySnapshot.isEmpty) {
                val user = querySnapshot.documents[0].toObject(User::class.java)
                Response.Success(user)
            } else {
                Response.Failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override suspend fun getUsersWithIdsFromFirestore(users: List<String>): GetUsersWithIdsResponse {
        val usersList = arrayListOf<User>()

        try {
            for (user in users) {
                val user = usersRef.document(user).get().await().toObject(User::class.java)
                    user?.let {
                        usersList.add(user)
                        println("users $usersList")
                    }
                }

            return Response.Success(usersList)
        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }






    override suspend fun deleteUserFromFirestore(userId: String) = try {
        usersRef.document(userId).delete().await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Failure(e)
    }

}