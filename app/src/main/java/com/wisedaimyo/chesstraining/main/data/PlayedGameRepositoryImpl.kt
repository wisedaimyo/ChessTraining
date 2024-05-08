package com.wisedaimyo.chesstraining.main.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.wisedaimyo.chesstraining.Constants
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.PlayedGame

import com.wisedaimyo.chesstraining.main.data.repository.AddPlayedGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetPlayedGameForUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetPlayedGameWithId
import com.wisedaimyo.chesstraining.main.data.repository.PlayedGameRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayedGameRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : PlayedGameRepository {
    override suspend fun addPlayedGame(playedGame: PlayedGame): AddPlayedGameResponse {
        return try {
            firestore
                .collection(Constants.PLAYED_GAMES)
                .add(playedGame)
                .await()

            Response.Success(true)

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getPlayedGamesForUser(
        userId: String
    ): GetPlayedGameForUserResponse {
        val listOfGames = mutableListOf<Pair<String, PlayedGame>>()
        return try {
            val querySnapshot =
                firestore
                    .collection(Constants.PLAYED_GAMES)
                    .where(
                        Filter.or(
                            Filter.equalTo("white", userId),
                            Filter.equalTo("black", userId),
                        )
                    )
                    .get()
                    .await()

            if (!querySnapshot.isEmpty) {
                for (snap in querySnapshot) {
                    val id = snap.id
                    val game = snap.toObject(PlayedGame::class.java)
                    listOfGames.add(Pair(id, game))
                }
                Response.Success(listOfGames.toList())
            } else {
                Response.Failure(Exception("Games not found"))
            }
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getPlayedGameWithId(gameId: String): GetPlayedGameWithId {
        return try {
            val querySnapshot = firestore
                .collection(Constants.PLAYED_GAMES)
                .document(gameId)
                .get()
                .await()

            if (querySnapshot != null) {
                val game = querySnapshot.toObject(PlayedGame::class.java)
                Response.Success(game)
            } else {
                Response.Failure(Exception("Game Not Found"))
            }

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }
}