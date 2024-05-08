package com.wisedaimyo.chesstraining.main.data.repository

import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.User
import kotlinx.coroutines.flow.Flow

typealias Users = List<User>
typealias UsersResponse = Response<Users>
typealias UsersIdResponse = Response<String>
typealias DeleteUserResponse = Response<Boolean>
typealias UpdateImageResponse = Response<Boolean>
typealias ResetEloResponse = Response<Boolean>
typealias UpdateNameResponse = Response<Boolean>
typealias UpdateEloResponse = Response<Boolean>
typealias AddUserResponse = Response<Boolean>
typealias GetCurrentUserResponse = Response<User>
typealias GetUserWithNameResponse = Response<User>
typealias GetUsersWithIdsResponse = Response<MutableList<User>>

interface UserRepository {
    fun getUsersFromFirestore(): Flow<UsersResponse>

    suspend fun updateEloToUserToFirestore(userId: String, elo: Int): UpdateEloResponse

    suspend fun getUsersId(userName: String): UsersIdResponse

    suspend fun updateImage(userId: String, image: String): UpdateImageResponse

    suspend fun resetElo(userId: String): ResetEloResponse

    suspend fun updateName(userId: String, name: String): UpdateNameResponse

    suspend fun addNewUserToFirestore(user: User, userId: String): AddUserResponse

    suspend fun getUserWithNameFromFirestore(userName: String): GetUserWithNameResponse

    suspend fun getUsersWithIdsFromFirestore(users: List<String>): GetUsersWithIdsResponse

    suspend fun getCurrentUserFromFirestore(userId: String): GetCurrentUserResponse

    suspend fun deleteUserFromFirestore(userId: String): DeleteUserResponse
}