package com.wisedaimyo.chesstraining.main.data.models.use_case.users

import com.wisedaimyo.chesstraining.main.data.repository.UserRepository

class GetCurrentUser(
    private val repo: UserRepository
) {
    suspend operator fun invoke(userId: String) = repo.getCurrentUserFromFirestore(userId)
}