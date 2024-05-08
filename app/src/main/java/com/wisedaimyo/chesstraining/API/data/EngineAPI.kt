package com.wisedaimyo.chesstraining.API.data

import com.wisedaimyo.chesstraining.API.data.model.NextMove
import retrofit2.http.GET
import retrofit2.http.Query

interface EngineAPI {
    @GET("stockfish.php")
    suspend fun getNextMove(
        @Query("fen") fenNotation: String,
        @Query("depth") depth: Int,
        @Query("mode") mode: String
    ): NextMove

    companion object {
        const val BASE_URL = "https://stockfish.online/api/"
    }
}
