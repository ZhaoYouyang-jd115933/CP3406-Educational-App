package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.data.mapper.toAcademicSourceOrNull
import com.youyangzhao.sourcesense.data.remote.api.CrossrefApiService
import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.domain.repository.AcademicSourceRepository

class CrossrefAcademicSourceRepository(
    private val apiService: CrossrefApiService
) : AcademicSourceRepository {

    override suspend fun searchSources(
        query: String,
        limit: Int
    ): List<AcademicSource> {
        val cleanQuery = query.trim()

        if (cleanQuery.isBlank()) {
            return emptyList()
        }

        val safeLimit = limit.coerceIn(
            minimumValue = 1,
            maximumValue = 10
        )

        val requestedRows = (
                safeLimit * 4
                ).coerceAtMost(40)

        val response = apiService.searchWorks(
            query = cleanQuery,
            rows = requestedRows
        )

        return response.message
            ?.items
            .orEmpty()
            .mapNotNull { workDto ->
                workDto.toAcademicSourceOrNull()
            }
            .filter { source ->
                source.isReadableOpenAccess
            }
            .distinctBy { source ->
                source.doi.lowercase()
            }
            .take(safeLimit)
    }
}

