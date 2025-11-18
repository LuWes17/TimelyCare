package com.example.wear.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.wear.data.settings.SettingsRepository
import com.example.wear.presentation.screens.*
import com.example.wear.presentation.screens.emergency.EmergencyScreen
import com.example.wear.presentation.screens.vitals.VitalsScreen
import com.example.wear.presentation.screens.medications.UpcomingScreen
import com.example.wear.presentation.theme.TimelyCareTheme

enum class Screen {
    HOME,
    SETTINGS,
    ALL_MEDS,
    EMERGENCY,
    VITALS,
    UPCOMING
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository.getInstance(context) }
    val medicationRepository = remember { com.example.wear.MedicationRepository.getInstance(context) }
    val settings by settingsRepository.settingsFlow.collectAsState(initial = com.example.wear.data.settings.AppSettings())

    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    TimelyCareTheme(
        accentColor = settings.accentColor,
        textSize = settings.textSize,
        isDarkMode = settings.isDarkMode
    ) {
        when (currentScreen) {
            Screen.HOME -> {
                HomeScreen(
                    onNavigateToScreen = { screenName ->
                        currentScreen = when (screenName) {
                            "Settings" -> Screen.SETTINGS
                            "All Meds" -> Screen.ALL_MEDS
                            "Emergency" -> Screen.EMERGENCY
                            "Vitals" -> Screen.VITALS
                            "Upcoming" -> Screen.UPCOMING
                            else -> Screen.HOME
                        }
                    },
                    settings = settings
                )
            }
            Screen.SETTINGS -> {
                SettingsScreen(
                    onBackClick = { currentScreen = Screen.HOME },
                    settingsRepository = settingsRepository
                )
            }
            Screen.ALL_MEDS -> {
                AllMedicationsScreen(
                    onBackClick = { currentScreen = Screen.HOME },
                    medicationRepository = medicationRepository
                )
            }
            Screen.EMERGENCY -> {
                EmergencyScreen(
                    onBackClick = { currentScreen = Screen.HOME }
                )
            }
            Screen.VITALS -> {
                VitalsScreen(
                    onBackClick = { currentScreen = Screen.HOME },
                    onHeartClick = {},
                    onTempClick = {},
                    onBpClick = {},
                    onGlucoseClick = {}
                )
            }
            Screen.UPCOMING -> {
                UpcomingScreen(
                    onBackClick = { currentScreen = Screen.HOME }
                )
            }
        }
    }
}