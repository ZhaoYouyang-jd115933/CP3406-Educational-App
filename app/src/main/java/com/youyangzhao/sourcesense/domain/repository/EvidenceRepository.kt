package com.youyangzhao.sourcesense.domain.repository

import com.youyangzhao.sourcesense.domain.model.EvidenceCase

interface EvidenceRepository {

    suspend fun getEvidenceCases(): List<EvidenceCase>

    suspend fun getEvidenceCase(
        caseId: String
    ): EvidenceCase?
}

