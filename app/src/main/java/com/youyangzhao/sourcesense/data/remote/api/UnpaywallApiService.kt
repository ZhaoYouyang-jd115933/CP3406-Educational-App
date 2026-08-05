package com.youyangzhao.sourcesense.data.remote.api

import com.youyangzhao.sourcesense.data.remote.dto.UnpaywallResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnpaywallApiService {

    @GET("v2/{doi}")
    suspend fun getOpenAccessInformation(
        @Path(
            value = "doi",
            encoded = true
        )
        doi: String,
        @Query("email")
        contactEmail: String
    ): UnpaywallResponseDto
}

