package com.wisedaimyo.chesstraining.API.data

import com.wisedaimyo.chesstraining.API.data.model.NextMove
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class NextMoveImpl(
    private var api: EngineAPI
): NextMoveRepository {

    override suspend fun getNextMove(
        fenNotation: String,
        depth: Int,
        mode: String
    ): Flow<Result<NextMove>> {
        return flow {
            val nextMoveFromAPI = try {
                api.getNextMove(fenNotation = fenNotation, depth = depth, mode = mode)

            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading next move"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading next move"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(message = "Unknown error"))
                return@flow
            }

            emit(Result.Success(nextMoveFromAPI))
        }
    }
}