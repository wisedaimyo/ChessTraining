package com.wisedaimyo.chesstraining.main.data.repository

import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.PlayedGame

typealias AddPlayedGameResponse = Response<Boolean>
typealias GetPlayedGameWithId = Response<PlayedGame>
typealias GetPlayedGameForUserResponse = Response<List<Pair<String, PlayedGame>>>

interface PlayedGameRepository {
    suspend fun addPlayedGame(playedGame: PlayedGame): AddPlayedGameResponse
    suspend fun getPlayedGamesForUser(userId: String): GetPlayedGameForUserResponse
    suspend fun getPlayedGameWithId(gameId: String): GetPlayedGameWithId
}