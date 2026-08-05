package com.youyangzhao.sourcesense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UnpaywallResponseDto(
    val doi: String?,
    val title: String?,
    @SerializedName("is_oa")
    val isOpenAccess: Boolean?,
    @SerializedName("oa_status")
    val openAccessStatus: String?,
    @SerializedName("best_oa_location")
    val bestOpenAccessLocation:
    UnpaywallLocationDto?
)

data class UnpaywallLocationDto(
    val url: String?,
    @SerializedName("url_for_pdf")
    val pdfUrl: String?,
    @SerializedName("url_for_landing_page")
    val landingPageUrl: String?,
    @SerializedName("host_type")
    val hostType: String?,
    val license: String?,
    val version: String?
)

