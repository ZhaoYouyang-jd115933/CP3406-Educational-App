package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.AcademicSource

interface AcademicSourceRepository {

    suspend fun searchSources(
        query: String,
        limit: Int = 10
    ): List<AcademicSource>
}