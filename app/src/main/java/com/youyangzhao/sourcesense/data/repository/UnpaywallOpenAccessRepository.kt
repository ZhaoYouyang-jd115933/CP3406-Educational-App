package com.youyangzhao.sourcesense.data.repository

import com.youyangzhao.sourcesense.data.mapper.toOpenAccessInfo
import com.youyangzhao.sourcesense.data.remote.api.UnpaywallApiService
import com.youyangzhao.sourcesense.domain.model.OpenAccessInfo
import com.youyangzhao.sourcesense.domain.repository.OpenAccessRepository

class UnpaywallOpenAccessRepository(
    private val apiService: UnpaywallApiService,
    private val contactEmail: String
) : OpenAccessRepository {

    init {
        require(
            contactEmail.isNotBlank() &&
                    contactEmail.contains("@")
        ) {
            "A valid contact email is required for Unpaywall."
        }
    }

    override suspend fun getOpenAccessInfo(
        doi: String
    ): OpenAccessInfo {
        val cleanDoi = doi
            .trim()
            .removePrefix("https://doi.org/")
            .removePrefix("http://doi.org/")
            .removePrefix("doi:")
            .trim()

        require(cleanDoi.isNotBlank()) {
            "A DOI is required."
        }

        val response =
            apiService.getOpenAccessInformation(
                doi = cleanDoi,
                contactEmail = contactEmail
            )

        return response.toOpenAccessInfo(
            requestedDoi = cleanDoi
        )
    }
}

