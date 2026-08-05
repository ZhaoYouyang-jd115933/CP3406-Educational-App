package com.youyangzhao.sourcesense.data.mapper

import com.youyangzhao.sourcesense.data.remote.dto.CrossrefAuthorDto
import com.youyangzhao.sourcesense.data.remote.dto.CrossrefDateDto
import com.youyangzhao.sourcesense.data.remote.dto.CrossrefLinkDto
import com.youyangzhao.sourcesense.data.remote.dto.CrossrefWorkDto
import com.youyangzhao.sourcesense.domain.model.AcademicSource

fun CrossrefWorkDto.toAcademicSourceOrNull():
        AcademicSource? {

    val cleanDoi = doi
        ?.trim()
        ?.takeIf { value ->
            value.isNotBlank()
        }
        ?: return null

    val cleanTitle = title
        ?.firstOrNull { value ->
            value.isNotBlank()
        }
        ?.cleanText()
        ?.takeIf { value ->
            value.isNotBlank()
        }
        ?: return null

    val authorNames = author
        .orEmpty()
        .mapNotNull { authorDto ->
            authorDto.toDisplayName()
        }
        .distinct()

    val year = published.extractYear()
        ?: publishedOnline.extractYear()
        ?: publishedPrint.extractYear()
        ?: issued.extractYear()

    val cleanPublicationName = containerTitle
        ?.firstOrNull { value ->
            value.isNotBlank()
        }
        ?.cleanText()
        ?.takeIf { value ->
            value.isNotBlank()
        }

    val cleanPublisher = publisher
        ?.cleanText()
        ?.takeIf { value ->
            value.isNotBlank()
        }

    val cleanAbstract = abstractText
        ?.removeMarkup()
        ?.takeIf { value ->
            value.isNotBlank()
        }

    val cleanSubjects = subject
        .orEmpty()
        .map { value ->
            value.cleanText()
        }
        .filter { value ->
            value.isNotBlank()
        }
        .distinct()

    val openLicenseUrl = license
        .orEmpty()
        .mapNotNull { licenseDto ->
            licenseDto.url.toValidWebUrl()
        }
        .firstOrNull { licenseUrl ->
            licenseUrl.isOpenAccessLicense()
        }

    val readableFullTextUrl = link
        .orEmpty()
        .mapNotNull { linkDto ->
            linkDto.toReadableLink()
        }
        .sortedBy { readableLink ->
            readableLink.priority
        }
        .firstOrNull()
        ?.url

    return AcademicSource(
        doi = cleanDoi,
        title = cleanTitle,
        authors = authorNames,
        publicationYear = year,
        publicationName = cleanPublicationName,
        publisher = cleanPublisher,
        sourceType = type
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            },
        url = url.toValidWebUrl()
            ?: "https://doi.org/$cleanDoi",
        abstractText = cleanAbstract,
        subjects = cleanSubjects,
        fullTextUrl = readableFullTextUrl,
        licenseUrl = openLicenseUrl
    )
}

private data class ReadableLink(
    val url: String,
    val priority: Int
)

private fun CrossrefLinkDto.toReadableLink():
        ReadableLink? {

    val cleanUrl =
        url.toValidWebUrl() ?: return null

    val cleanContentType =
        contentType.orEmpty().lowercase()

    val priority = when {
        cleanContentType.contains("html") -> 0
        cleanContentType.contains("pdf") -> 1
        else -> 2
    }

    return ReadableLink(
        url = cleanUrl,
        priority = priority
    )
}

private fun String.isOpenAccessLicense(): Boolean {
    return contains(
        other = "creativecommons.org",
        ignoreCase = true
    ) || contains(
        other = "open-access",
        ignoreCase = true
    ) || contains(
        other = "openaccess",
        ignoreCase = true
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

private fun CrossrefAuthorDto.toDisplayName():
        String? {

    val fullName = listOfNotNull(
        given
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            },
        family
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            }
    ).joinToString(" ")

    return fullName.takeIf { value ->
        value.isNotBlank()
    }
}

private fun CrossrefDateDto?.extractYear():
        Int? {

    return this
        ?.dateParts
        ?.firstOrNull()
        ?.firstOrNull()
}

private fun String.cleanText(): String {
    return replace(
        Regex("\\s+"),
        " "
    ).trim()
}

private fun String.removeMarkup(): String {
    return replace(
        Regex("<[^>]*>"),
        " "
    )
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .cleanText()
}

