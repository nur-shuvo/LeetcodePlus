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
