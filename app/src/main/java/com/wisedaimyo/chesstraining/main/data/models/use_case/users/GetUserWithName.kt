package com.wisedaimyo.chesstraining.main.data.models.use_case.users

import com.wisedaimyo.chesstraining.main.data.repository.UserRepository

class GetUserWithName(
    private val repo: UserRepository
) {
    suspend operator fun invoke(userName: String) = repo.getUserWithNameFromFirestore(userName)
}