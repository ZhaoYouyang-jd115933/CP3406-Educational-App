package com.youyangzhao.sourcesense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CrossrefWorksResponseDto(
    val status: String?,
    @SerializedName("message-type")
    val messageType: String?,
    @SerializedName("message-version")
    val messageVersion: String?,
    val message: CrossrefWorksMessageDto?
)

data class CrossrefWorksMessageDto(
    @SerializedName("total-results")
    val totalResults: Int?,
    @SerializedName("items-per-page")
    val itemsPerPage: Int?,
    val items: List<CrossrefWorkDto>?
)

data class CrossrefWorkDto(
    @SerializedName("DOI")
    val doi: String?,
    val type: String?,
    val title: List<String>?,
    val author: List<CrossrefAuthorDto>?,
    @SerializedName("container-title")
    val containerTitle: List<String>?,
    val publisher: String?,
    val published: CrossrefDateDto?,
    @SerializedName("published-online")
    val publishedOnline: CrossrefDateDto?,
    @SerializedName("published-print")
    val publishedPrint: CrossrefDateDto?,
    val issued: CrossrefDateDto?,
    @SerializedName("URL")
    val url: String?,
    @SerializedName("abstract")
    val abstractText: String?,
    val subject: List<String>?,
    val link: List<CrossrefLinkDto>?,
    val license: List<CrossrefLicenseDto>?
)

data class CrossrefAuthorDto(
    val given: String?,
    val family: String?,
    val sequence: String?,
    @SerializedName("ORCID")
    val orcid: String?
)

data class CrossrefDateDto(
    @SerializedName("date-parts")
    val dateParts: List<List<Int>>?
)

data class CrossrefLinkDto(
    @SerializedName("URL")
    val url: String?,
    @SerializedName("content-type")
    val contentType: String?,
    @SerializedName("content-version")
    val contentVersion: String?,
    @SerializedName("intended-application")
    val intendedApplication: String?
)

data class CrossrefLicenseDto(
    @SerializedName("URL")
    val url: String?,
    @SerializedName("content-version")
    val contentVersion: String?,
    @SerializedName("delay-in-days")
    val delayInDays: Int?
)

