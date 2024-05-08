package com.wisedaimyo.chesstraining.main.data.repository

import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.PlayedGame
import com.wisedaimyo.chesstraining.main.data.models.model.PlayingGame

typealias MakeMoveInGameResponse = Response<Boolean>
typealias GetGameWithIdResponse = Response<PlayingGame>
typealias GetGamesForUserResponse = Response<List<Pair<String, PlayingGame>>>
typealias CreateGameForUsersResponse = Response<Boolean>
typealias UpdateGameResponse = Response<Boolean>
typealias RemoveGameResponse = Response<Boolean>
typealias OfferDrawResponse = Response<Boolean>
typealias AcceptOrDeclineDrawResponse = Response<Boolean>

interface PlayingGameRepository {
    suspend fun getGamesForUser(userId: String): GetGamesForUserResponse
    suspend fun updateGame(gameId: String, game: PlayingGame): UpdateGameResponse
    suspend fun createGameForUsers(firstUser: String, secondUser: String, time: Int): CreateGameForUsersResponse
    suspend fun getGameWithId(gameId: String): GetGameWithIdResponse
    suspend fun makeMoveInGame(gameId: String, move: String): MakeMoveInGameResponse
    suspend fun removeGame(gameId: String): RemoveGameResponse
    suspend fun offerDraw(gameId: String): OfferDrawResponse
    suspend fun acceptOrDeclineDraw(gameId: String, isAccepted: Boolean): AcceptOrDeclineDrawResponse
}