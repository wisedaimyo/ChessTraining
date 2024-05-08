package com.wisedaimyo.chesstraining.API.data

import com.wisedaimyo.chesstraining.API.data.model.NextMove
import kotlinx.coroutines.flow.Flow

interface NextMoveRepository {
    suspend fun getNextMove(fenNotation: String, depth: Int, mode: String): Flow<Result<NextMove>>
}