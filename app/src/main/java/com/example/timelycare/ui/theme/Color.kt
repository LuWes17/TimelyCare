package com.example.timelycare.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// Dynamic palette so Settings can adjust accent and dark mode globally.
object TimelyDynamicColors {
    var primary by mutableStateOf(Color(0xFF4285F4))
    var primaryLight by mutableStateOf(Color(0xFF6DA4F7))
    var primaryDark by mutableStateOf(Color(0xFF0D47A1))

    var background by mutableStateOf(Color(0xFFF5F5F5))
    var white by mutableStateOf(Color(0xFFFFFFFF))
    var gray by mutableStateOf(Color(0xFF9E9E9E))
    var textPrimary by mutableStateOf(Color(0xFF212121))
    var textSecondary by mutableStateOf(Color(0xFF757575))
    var gray200 by mutableStateOf(Color(0xFFE2E8F0))
}

// TimelyCare Color Palette (backed by dynamic colors)
val TimelyCareBlue: Color get() = TimelyDynamicColors.primary
val TimelyCareBlueLight: Color get() = TimelyDynamicColors.primaryLight
val TimelyCareBlueDark: Color get() = TimelyDynamicColors.primaryDark // Darker shade of blue
val TimelyCareBackground: Color get() = TimelyDynamicColors.background
val TimelyCareWhite: Color get() = TimelyDynamicColors.white
val TimelyCareGray: Color get() = TimelyDynamicColors.gray
val TimelyCareTextPrimary: Color get() = TimelyDynamicColors.textPrimary
val TimelyCareTextSecondary: Color get() = TimelyDynamicColors.textSecondary
val TimelyCareGray200: Color get() = TimelyDynamicColors.gray200

// Legacy colors for compatibility
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)