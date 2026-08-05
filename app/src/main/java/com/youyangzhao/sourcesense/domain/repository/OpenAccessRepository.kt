package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.OpenAccessInfo

interface OpenAccessRepository {

    suspend fun getOpenAccessInfo(
        doi: String
    ): OpenAccessInfo
}