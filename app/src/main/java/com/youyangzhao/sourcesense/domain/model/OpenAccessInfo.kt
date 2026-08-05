package com.youyangzhao.sourcesense.domain.model

data class OpenAccessInfo(
    val doi: String,
    val isOpenAccess: Boolean,
    val openAccessStatus: String? = null,
    val pdfUrl: String? = null,
    val landingPageUrl: String? = null,
    val openAccessUrl: String? = null,
    val hostType: String? = null,
    val license: String? = null,
    val version: String? = null
) {
    val bestReadUrl: String?
        get() = pdfUrl
            ?: landingPageUrl
            ?: openAccessUrl

    val hasFreeFullText: Boolean
        get() = isOpenAccess &&
                !bestReadUrl.isNullOrBlank()

    val hasDirectPdf: Boolean
        get() = !pdfUrl.isNullOrBlank()

    val statusDisplay: String
        get() = openAccessStatus
            ?.replace("-", " ")
            ?.replaceFirstChar { character ->
                character.uppercase()
            }
            ?: if (isOpenAccess) {
                "Open access"
            } else {
                "Closed access"
            }

    val hostTypeDisplay: String
        get() = hostType
            ?.replace("-", " ")
            ?.replaceFirstChar { character ->
                character.uppercase()
            }
            ?: "Host information unavailable"

    val licenseDisplay: String
        get() = license
            ?.takeIf { value ->
                value.isNotBlank()
            }
            ?: "License information unavailable"

    init {
        require(doi.isNotBlank()) {
            "Open access information must have a DOI."
        }
    }
}