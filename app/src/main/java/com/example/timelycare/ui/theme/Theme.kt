package com.example.timelycare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.SettingsRepository
import com.example.timelycare.data.UserSettings
import com.example.timelycare.ui.theme.TimelyDynamicColors

val LocalUserSettings = staticCompositionLocalOf { UserSettings() }

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TimelyCareTheme(
    darkTheme: Boolean? = null,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository.getInstance(context) }
    val settings by settingsRepository.settings.collectAsStateWithLifecycle()
    val useDarkTheme = darkTheme ?: settings.darkModeEnabled || isSystemInDarkTheme()

    // Map theme color index to accent palette (order must match SettingsScreen)
    val accentPalettes = listOf<Triple<Color, Color, Color>>(
        Triple(Color(0xFF00C853), Color(0xFF4CAF50), Color(0xFF2E7D32)), // Green
        Triple(Color(0xFF2196F3), Color(0xFF64B5F6), Color(0xFF1565C0)), // Blue
        Triple(Color(0xFF9C27B0), Color(0xFFBA68C8), Color(0xFF6A1B9A)), // Purple
        Triple(Color(0xFFF44336), Color(0xFFE57373), Color(0xFFB71C1C)), // Red
        Triple(Color(0xFFFF9800), Color(0xFFFFB74D), Color(0xFFE65100)), // Orange
        Triple(Color(0xFFE91E63), Color(0xFFF06292), Color(0xFFC2185B))  // Pink
    )
    val accent = accentPalettes.getOrElse(settings.themeColorIndex) { accentPalettes.first() }

    // Update dynamic colors based on settings
    TimelyDynamicColors.primary = accent.first
    TimelyDynamicColors.primaryLight = accent.second
    TimelyDynamicColors.primaryDark = accent.third

    if (useDarkTheme) {
        TimelyDynamicColors.background = Color(0xFF0F172A)
        TimelyDynamicColors.white = Color(0xFF111827)
        TimelyDynamicColors.gray = Color(0xFF9CA3AF)
        TimelyDynamicColors.gray200 = Color(0xFF374151)
        TimelyDynamicColors.textPrimary = Color(0xFFF9FAFB)
        TimelyDynamicColors.textSecondary = Color(0xFFE5E7EB)
    } else {
        TimelyDynamicColors.background = Color(0xFFF5F5F5)
        TimelyDynamicColors.white = Color(0xFFFFFFFF)
        TimelyDynamicColors.gray = Color(0xFF9E9E9E)
        TimelyDynamicColors.gray200 = Color(0xFFE2E8F0)
        TimelyDynamicColors.textPrimary = Color(0xFF111827)
        TimelyDynamicColors.textSecondary = Color(0xFF6B7280)
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalUserSettings provides settings
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}