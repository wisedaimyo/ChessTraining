package com.wisedaimyo.chesstraining.main.data.models

sealed class Response<out T> {

    object Loading: Response<Nothing>()

    data class Success<out T>(
        val data: T?
    ): Response<T>()

    data class Failure(
        val e: Exception?
    ): Response<Nothing>()
}