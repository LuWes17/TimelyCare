package com.example.wear.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.wear.data.settings.SettingsRepository
import com.example.wear.presentation.screens.*
import com.example.wear.presentation.screens.emergency.EmergencyScreen
import com.example.wear.presentation.screens.vitals.VitalsScreen
import com.example.wear.presentation.screens.medications.UpcomingScreen
import com.example.wear.presentation.screens.MedicineReminderScreen
import com.example.wear.presentation.theme.TimelyCareTheme
import com.example.wear.MedicineReminder
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

enum class Screen {
    HOME,
    SETTINGS,
    ALL_MEDS,
    EMERGENCY,
    VITALS,
    UPCOMING,
    REMINDER
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository.getInstance(context) }
    val medicationRepository = remember { com.example.wear.MedicationRepository.getInstance(context) }
    val settings by settingsRepository.settingsFlow.collectAsState(initial = com.example.wear.data.settings.AppSettings())
    val activeReminder by medicationRepository.activeReminder.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    // 5-second reminder simulation
    LaunchedEffect(Unit) {
        delay(5000) // 5 seconds
        val simulatedReminder = MedicineReminder(
            id = "sim_reminder_1",
            medicineName = "Biogesic",
            dosage = "50mg",
            scheduledTime = "Now"
        )
        medicationRepository.triggerReminder(simulatedReminder)
    }

    TimelyCareTheme(
        accentColor = settings.accentColor,
        textSize = settings.textSize,
        isDarkMode = settings.isDarkMode
    ) {
        // Show the current screen
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
            Screen.REMINDER -> {
                // This case shouldn't be reached as reminder is shown as overlay
            }
        }

        // Show reminder overlay when active
        activeReminder?.let { reminder ->
            MedicineReminderScreen(
                medicineName = reminder.medicineName,
                dosage = reminder.dosage,
                onDismiss = {
                    medicationRepository.dismissReminder()
                },
                onConfirm = {
                    medicationRepository.confirmReminder()
                }
            )
        }
    }
}