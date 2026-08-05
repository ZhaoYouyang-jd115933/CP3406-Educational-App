package com.youyangzhao.sourcesense.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object CrossrefApiClient {

    private const val BASE_URL =
        "https://api.crossref.org/"

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain
                    .request()
                    .newBuilder()
                    .header(
                        "Accept",
                        "application/json"
                    )
                    .header(
                        "User-Agent",
                        "SourceSense/1.0"
                    )
                    .build()

                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(
                20,
                TimeUnit.SECONDS
            )
            .readTimeout(
                20,
                TimeUnit.SECONDS
            )
            .build()

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val service: CrossrefApiService by lazy {
        retrofit.create(
            CrossrefApiService::class.java
        )
    }
}

