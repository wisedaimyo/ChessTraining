package com.wisedaimyo.chesstraining.main.data.models.use_case.users

import com.wisedaimyo.chesstraining.main.data.repository.UserRepository

class GetUsersWithIds(
    private val repo: UserRepository
) {
    suspend operator fun invoke(mutableList: MutableList<String>) = repo.getUsersWithIdsFromFirestore(mutableList.toList())
}