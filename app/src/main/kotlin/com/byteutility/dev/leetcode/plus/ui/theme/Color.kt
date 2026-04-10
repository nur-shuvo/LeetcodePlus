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

val ProblemsGreen = Color(0xFF006D46)
val BadgeRed = Color(0xFFB3261E)
val TopBarBackground = Color(0xFFF9FAF9)
val SearchBarBackground = Color(0xFFF0F1F1)
val SearchBarPlaceholder = Color(0xFF747878)
val CardBorderColor = Color(0xFFC4C7C7).copy(alpha = 0.3f)
val ProblemNumberColor = Color(0xFF444747).copy(alpha = 0.6f)
val TitleColor = Color(0xFF191C1C)
val LabelColor = Color(0xFF444747)
val TagBackground = Color(0xFFE8E9E9)
val TagText = Color(0xFF444747)
val EasyBg = Color(0xFFCDEDD9)
val EasyText = Color(0xFF006D46)
val MediumBg = Color(0xFFFFDCBE)
val MediumText = Color(0xFF825500)
val HardBg = Color(0xFFFFDAD6)
val HardText = Color(0xFF904A44)
val OverflowTagBg = Color(0xFFCDEDD9)
val OverflowTagBorder = Color(0xFF006D46).copy(alpha = 0.2f)
val premiumLockColor = Color(0xFF825500)
