package com.youyangzhao.sourcesense.data.mapper

import com.youyangzhao.sourcesense.data.remote.dto.CrossrefAuthorDto
import com.youyangzhao.sourcesense.data.remote.dto.CrossrefDateDto
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
        url = url
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            }
            ?: "https://doi.org/$cleanDoi",
        abstractText = cleanAbstract,
        subjects = cleanSubjects
    )
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

