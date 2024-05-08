package com.wisedaimyo.chesstraining.API

import com.wisedaimyo.chesstraining.API.data.EngineAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val api: EngineAPI = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(EngineAPI.BASE_URL)
        .client(client)
        .build()
        .create(EngineAPI::class.java)

}
