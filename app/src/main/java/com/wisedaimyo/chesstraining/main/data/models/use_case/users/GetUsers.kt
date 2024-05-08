package com.wisedaimyo.chesstraining.main.data.models.use_case.users

import com.wisedaimyo.chesstraining.main.data.repository.UserRepository

class GetUsers(
    private val repo: UserRepository
) {
    operator fun invoke() = repo.getUsersFromFirestore()
}