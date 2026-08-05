package com.youyangzhao.sourcesense.data.remote.api

import com.youyangzhao.sourcesense.data.remote.dto.CrossrefWorksResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CrossrefApiService {

    @GET("works")
    suspend fun searchWorks(
        @Query("query.bibliographic")
        query: String,
        @Query("rows")
        rows: Int = 10,
        @Query("mailto")
        contactEmail: String? = null
    ): CrossrefWorksResponseDto
}

