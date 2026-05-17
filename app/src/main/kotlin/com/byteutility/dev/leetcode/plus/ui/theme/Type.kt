package com.byteutility.dev.leetcode.plus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.byteutility.dev.leetcode.plus.R

val RobotoFontFamily = FontFamily(
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_reguler, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_semi_bold, FontWeight.SemiBold),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_thin, FontWeight.Thin),
)

val baseline = Typography()

val AppTypography = Typography(
    // Display: Large, short, important text (e.g., hero headers)
    displayLarge = baseline.displayLarge.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Bold),
    displayMedium = baseline.displayMedium.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Bold),
    displaySmall = baseline.displaySmall.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.SemiBold),

    // Headlines: Significant text that marks sections
    headlineLarge = baseline.headlineLarge.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.SemiBold),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.SemiBold),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Medium),

    // Titles: Shorter than headlines, used for medium-emphasis (e.g., TopAppBar)
    titleLarge = baseline.titleLarge.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Medium),
    titleMedium = baseline.titleMedium.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Medium),
    titleSmall = baseline.titleSmall.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Medium),

    // Body: Longer passages of text
    bodyLarge = baseline.bodyLarge.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Normal),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Normal),
    bodySmall = baseline.bodySmall.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Light),

    // Labels: Small, functional text (e.g., components, buttons, captions)
    labelLarge = baseline.labelLarge.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Medium),
    labelMedium = baseline.labelMedium.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Medium),
    labelSmall = baseline.labelSmall.copy(fontFamily = RobotoFontFamily, fontWeight = FontWeight.Medium),
)
