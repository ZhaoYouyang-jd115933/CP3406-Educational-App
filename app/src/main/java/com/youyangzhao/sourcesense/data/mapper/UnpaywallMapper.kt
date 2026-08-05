package com.youyangzhao.sourcesense.data.mapper

import com.youyangzhao.sourcesense.data.remote.dto.UnpaywallResponseDto
import com.youyangzhao.sourcesense.domain.model.OpenAccessInfo

fun UnpaywallResponseDto.toOpenAccessInfo(
    requestedDoi: String
): OpenAccessInfo {
    val cleanDoi = doi
        ?.trim()
        ?.takeIf { value ->
            value.isNotBlank()
        }
        ?: requestedDoi.trim()

    val location = bestOpenAccessLocation

    return OpenAccessInfo(
        doi = cleanDoi,
        isOpenAccess = isOpenAccess == true,
        openAccessStatus = openAccessStatus
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            },
        pdfUrl = location
            ?.pdfUrl
            .toValidWebUrl(),
        landingPageUrl = location
            ?.landingPageUrl
            .toValidWebUrl(),
        openAccessUrl = location
            ?.url
            .toValidWebUrl(),
        hostType = location
            ?.hostType
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            },
        license = location
            ?.license
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            },
        version = location
            ?.version
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            }
    )
}

private fun String?.toValidWebUrl(): String? {
    val cleanUrl = this
        ?.trim()
        ?.takeIf { value ->
            value.isNotBlank()
        }
        ?: return null

    return cleanUrl.takeIf { value ->
        value.startsWith(
            prefix = "https://",
            ignoreCase = true
        ) || value.startsWith(
            prefix = "http://",
            ignoreCase = true
        )
    }
}

