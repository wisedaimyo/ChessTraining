package com.wisedaimyo.chesstraining.main.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.wisedaimyo.chesstraining.Constants.PLAYING_CHESS
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.model.PlayingGame
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.data.repository.AcceptOrDeclineDrawResponse
import com.wisedaimyo.chesstraining.main.data.repository.CreateGameForUsersResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetGameWithIdResponse
import com.wisedaimyo.chesstraining.main.data.repository.GetGamesForUserResponse
import com.wisedaimyo.chesstraining.main.data.repository.MakeMoveInGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.OfferDrawResponse
import com.wisedaimyo.chesstraining.main.data.repository.PlayingGameRepository
import com.wisedaimyo.chesstraining.main.data.repository.RemoveGameResponse
import com.wisedaimyo.chesstraining.main.data.repository.UpdateGameResponse
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PlayingGameRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : PlayingGameRepository {

    override suspend fun getGamesForUser(userId: String): GetGamesForUserResponse {
        val listOfGames = mutableListOf<Pair<String, PlayingGame>>()
        return try {
            val querySnapshot =
                fireStore
                     .collection(PLAYING_CHESS)
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
                    val game = snap.toObject(PlayingGame::class.java)
                    listOfGames.add(Pair(id, game))
                }
                Response.Success(listOfGames.toList())
            } else {
                Response.Failure(Exception("User not found"))
            }

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun updateGame(gameId: String, game: PlayingGame): UpdateGameResponse {
       return try {
           fireStore.collection(PLAYING_CHESS).document(gameId).set(game).await()
           Response.Success(true)
       } catch (e: Exception) {
           Response.Failure(e)
       }
    }

    override suspend fun createGameForUsers(
        firstUser: String,
        secondUser: String,
        time: Int
    ): CreateGameForUsersResponse {
        val whoWhite = Random.nextBoolean()

        val newChessGame =
            PlayingGame(
                black = if(!whoWhite) firstUser else secondUser,
                white = if(whoWhite) firstUser else secondUser,
                lastMove = Timestamp.now(),
                fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                moves = listOf(),
                time = time
           )

        return try {
            fireStore
                .collection(PLAYING_CHESS)
                .add(newChessGame)
                .await()

            Response.Success(true)

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getGameWithId(gameId: String): GetGameWithIdResponse {
        return try {
            val querySnapshot = fireStore
                .collection(PLAYING_CHESS)
                .document(gameId)
                .get()
                .await()

            if (querySnapshot != null) {
                val game = querySnapshot.toObject(PlayingGame::class.java)
                Response.Success(game)
            } else {
                Response.Failure(Exception("Game Not Found"))
            }

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun makeMoveInGame(gameId: String, move: String): MakeMoveInGameResponse {
        TODO("Not yet implemented")
    }

    override suspend fun removeGame(gameId: String): RemoveGameResponse {
        return try {
            fireStore.collection(PLAYING_CHESS).document(gameId).delete().await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun offerDraw(gameId: String): OfferDrawResponse {
        TODO("Not yet implemented")

    }

    override suspend fun acceptOrDeclineDraw(
        gameId: String,
        isAccepted: Boolean,
    ): AcceptOrDeclineDrawResponse {
        TODO("Not yet implemented")



    }


}