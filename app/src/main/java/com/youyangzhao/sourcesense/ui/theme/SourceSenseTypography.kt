package com.youyangzhao.sourcesense.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

private val DefaultTypography = Typography()

private val LargerTextTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(
        fontSize = 64.sp,
        lineHeight = 72.sp
    ),
    displayMedium = DefaultTypography.displayMedium.copy(
        fontSize = 51.sp,
        lineHeight = 59.sp
    ),
    displaySmall = DefaultTypography.displaySmall.copy(
        fontSize = 41.sp,
        lineHeight = 49.sp
    ),
    headlineLarge = DefaultTypography.headlineLarge.copy(
        fontSize = 37.sp,
        lineHeight = 45.sp
    ),
    headlineMedium = DefaultTypography.headlineMedium.copy(
        fontSize = 33.sp,
        lineHeight = 41.sp
    ),
    headlineSmall = DefaultTypography.headlineSmall.copy(
        fontSize = 29.sp,
        lineHeight = 37.sp
    ),
    titleLarge = DefaultTypography.titleLarge.copy(
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    titleMedium = DefaultTypography.titleMedium.copy(
        fontSize = 19.sp,
        lineHeight = 27.sp
    ),
    titleSmall = DefaultTypography.titleSmall.copy(
        fontSize = 17.sp,
        lineHeight = 25.sp
    ),
    bodyLarge = DefaultTypography.bodyLarge.copy(
        fontSize = 19.sp,
        lineHeight = 28.sp
    ),
    bodyMedium = DefaultTypography.bodyMedium.copy(
        fontSize = 17.sp,
        lineHeight = 25.sp
    ),
    bodySmall = DefaultTypography.bodySmall.copy(
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    labelLarge = DefaultTypography.labelLarge.copy(
        fontSize = 17.sp,
        lineHeight = 23.sp
    ),
    labelMedium = DefaultTypography.labelMedium.copy(
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    labelSmall = DefaultTypography.labelSmall.copy(
        fontSize = 13.sp,
        lineHeight = 19.sp
    )
)

fun sourceSenseTypography(
    useLargerText: Boolean
): Typography {
    // Select one typography scale for the whole app
    return if (useLargerText) {
        LargerTextTypography
    } else {
        DefaultTypography
    }
}

