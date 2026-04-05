package com.byteutility.dev.leetcode.plus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val GreenLight = Color(0xFFE0F7FA)
val YellowLight = Color(0xFFFFF9C4)
val RedLight = Color(0xFFFFCDD2)

val DeepTealDark = Color(0xFF004D40)
val DarkAmberDark = Color(0xFF5D4037)
val DeepMaroonDark = Color(0xFFB71C1C)

val ColorScheme.easyCategory: Color
    @Composable get() = if (isSystemInDarkTheme()) DeepTealDark else GreenLight

val ColorScheme.mediumCategory: Color
    @Composable get() = if (isSystemInDarkTheme()) DarkAmberDark else YellowLight

val ColorScheme.hardCategory: Color
    @Composable get() = if (isSystemInDarkTheme()) DeepMaroonDark else RedLight

// Profile/section card gradient
val ColorScheme.sectionGradientStart: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2E7D32) else Color(0xFF4CAF50)

val ColorScheme.sectionGradientEnd: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF424242) else Color.LightGray

// "All Problems" FAB gradient
val ColorScheme.allProblemsGradientStart: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1A6B7A) else Color(0xFF6dd5ed)

val ColorScheme.allProblemsGradientEnd: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF0D3D4A) else Color(0xFF2193b0)

// Bottom nav gradient
val ColorScheme.navBarGradientStart: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2E4A35) else Color(0xFFBECEC3)

val ColorScheme.navBarGradientMiddle: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1B5E30) else Color(0xFF498A5C)

val ColorScheme.navBarGradientEnd: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2E4A35) else Color(0xFFBECEC3)

// Goal status backgrounds
val ColorScheme.goalCompletedBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1B3A1E) else Color(0xFFE8F5E9)

val ColorScheme.goalInProgressBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF3A3520) else Color(0xFFFFF8E1)

val ColorScheme.goalDefaultBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2C2C2C) else Color(0xFFF5F5F5)

// Goal status text
val ColorScheme.goalCompletedText: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF81C784) else Color(0xFF4CAF50)

val ColorScheme.goalInProgressText: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFFCC02) else Color(0xFFFFA000)

val ColorScheme.goalDefaultText: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFBDBDBD) else Color(0xFF757575)

// Search field background
val ColorScheme.searchFieldBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1A2733) else Color(0xFFE3F2FD)

// Video thumbnail placeholder
val ColorScheme.thumbnailPlaceholder: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF424242) else Color.Gray
