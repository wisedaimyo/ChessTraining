package com.wisedaimyo.chesstraining.main.data.models.use_case.users

import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.repository.UserRepository

class AddUser(
    private val repo: UserRepository
) {
    suspend operator fun invoke(user: User, userId: String) = repo.addNewUserToFirestore(user, userId)
}