package com.youyangzhao.sourcesense.domain.model

data class AcademicSource(
    val doi: String,
    val title: String,
    val authors: List<String> = emptyList(),
    val publicationYear: Int? = null,
    val publicationName: String? = null,
    val publisher: String? = null,
    val sourceType: String? = null,
    val url: String? = null,
    val abstractText: String? = null,
    val subjects: List<String> = emptyList(),
    val fullTextUrl: String? = null,
    val licenseUrl: String? = null
) {
    val authorsDisplay: String
        get() = if (authors.isEmpty()) {
            "Author information unavailable"
        } else {
            authors.joinToString(", ")
        }

    val yearDisplay: String
        get() = publicationYear?.toString()
            ?: "Publication year unavailable"

    val publicationDisplay: String
        get() = publicationName
            ?.takeIf { it.isNotBlank() }
            ?: "Publication information unavailable"

    val publisherDisplay: String
        get() = publisher
            ?.takeIf { it.isNotBlank() }
            ?: "Publisher information unavailable"

    val typeDisplay: String
        get() = sourceType
            ?.replace("-", " ")
            ?.replaceFirstChar { character ->
                character.uppercase()
            }
            ?: "Source type unavailable"

    val hasAbstract: Boolean
        get() = !abstractText.isNullOrBlank()

    val isReadableOpenAccess: Boolean
        get() {
            return !fullTextUrl.isNullOrBlank() &&
                    !licenseUrl.isNullOrBlank()
        }

    init {
        require(doi.isNotBlank()) {
            "An academic source must have a DOI."
        }

        require(title.isNotBlank()) {
            "An academic source must have a title."
        }
    }
}

