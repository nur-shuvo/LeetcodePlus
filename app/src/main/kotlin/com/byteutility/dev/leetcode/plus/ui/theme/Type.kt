package com.byteutility.dev.leetcode.plus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.byteutility.dev.leetcode.plus.R


val FiraCodeFontFamily = FontFamily(
    Font(R.font.fira_code_light, FontWeight.Light),
    Font(R.font.fira_code_regular, FontWeight.Normal),
    Font(R.font.fira_code_medium, FontWeight.Medium),
    Font(R.font.fira_code_semi_bold, FontWeight.SemiBold),
    Font(R.font.fira_code_bold, FontWeight.Bold),
)

val baseline = Typography()

val AppTypography = Typography(
    // Display: Large, short, important text (e.g., hero headers)
    displayLarge = baseline.displayLarge.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Bold),
    displayMedium = baseline.displayMedium.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Bold),
    displaySmall = baseline.displaySmall.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.SemiBold),

    // Headlines: Significant text that marks sections
    headlineLarge = baseline.headlineLarge.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.SemiBold),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.SemiBold),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Medium),

    // Titles: Shorter than headlines, used for medium-emphasis (e.g., TopAppBar)
    titleLarge = baseline.titleLarge.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Medium),
    titleMedium = baseline.titleMedium.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Medium),
    titleSmall = baseline.titleSmall.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Medium),

    // Body: Longer passages of text
    bodyLarge = baseline.bodyLarge.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Normal),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Normal),
    bodySmall = baseline.bodySmall.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Light),

    // Labels: Small, functional text (e.g., components, buttons, captions)
    labelLarge = baseline.labelLarge.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Medium),
    labelMedium = baseline.labelMedium.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Medium),
    labelSmall = baseline.labelSmall.copy(fontFamily = FiraCodeFontFamily, fontWeight = FontWeight.Medium),
)
