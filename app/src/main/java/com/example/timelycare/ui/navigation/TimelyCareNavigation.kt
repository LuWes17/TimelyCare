package com.example.timelycare.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.timelycare.ui.theme.TimelyCareBackground
import com.example.timelycare.ui.components.TimelyCareBottomNavigation
import com.example.timelycare.ui.components.TimelyCareTopBar
import com.example.timelycare.ui.components.AddMedicineHeader
import com.example.timelycare.ui.components.MedicationsHeader
import com.example.timelycare.ui.components.CalendarHeader
import com.example.timelycare.ui.components.ContactsHeader
import com.example.timelycare.ui.screens.dashboard.DashboardScreen
import com.example.timelycare.ui.screens.medications.MedicationsScreen
import com.example.timelycare.ui.screens.medications.AddEditMedicationScreen
import com.example.timelycare.ui.screens.calendar.CalendarScreen
import com.example.timelycare.ui.screens.contacts.ContactsScreen
import com.example.timelycare.ui.screens.heartrate.HeartRateScreen
import com.example.timelycare.ui.screens.bloodpressure.BloodPressureScreen
import com.example.timelycare.ui.screens.glucose.GlucoseScreen
import com.example.timelycare.ui.screens.settings.SettingsScreen
import com.example.timelycare.ui.components.SettingsHeader
import com.example.timelycare.data.Medication

@Composable
fun TimelyCareApp() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddMedicine by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<Medication?>(null) }
    var showHeartRateScreen by remember { mutableStateOf(false) }
    var showBloodPressureScreen by remember { mutableStateOf(false) }
    var showGlucoseScreen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            when {
                showAddMedicine -> AddMedicineHeader(
                    isEditing = editingMedication != null,
                    onBackClick = {
                        showAddMedicine = false
                        editingMedication = null
                    }
                )
                showHeartRateScreen -> null
                showBloodPressureScreen -> null
                showGlucoseScreen -> null
                showSettingsScreen -> SettingsHeader(
                    onBackClick = { showSettingsScreen = false }
                )
                selectedTabIndex == 1 -> MedicationsHeader(onAddClick = { showAddMedicine = true })
                selectedTabIndex == 2 -> CalendarHeader(onSettingsClick = { showSettingsScreen = true })
                selectedTabIndex == 3 -> ContactsHeader(onSettingsClick = { showSettingsScreen = true })
                else -> TimelyCareTopBar(onSettingsClick = { showSettingsScreen = true })
            }
        },
        bottomBar = {
            if (!showAddMedicine && !showHeartRateScreen && !showBloodPressureScreen && !showGlucoseScreen && !showSettingsScreen) {
                TimelyCareBottomNavigation(
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
            }
        },
        containerColor = TimelyCareBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                showSettingsScreen -> SettingsScreen(

                )
                showAddMedicine -> AddEditMedicationScreen(
                    editingMedication = editingMedication,
                    onSave = {
                        showAddMedicine = false
                        editingMedication = null
                    }
                )
                showHeartRateScreen -> HeartRateScreen(
                    onBackClick = { showHeartRateScreen = false }
                )
                showBloodPressureScreen -> BloodPressureScreen(
                    onBackClick = { showBloodPressureScreen = false }
                )
                showGlucoseScreen -> GlucoseScreen(
                    onBackClick = { showGlucoseScreen = false }
                )
                selectedTabIndex == 0 -> DashboardScreen(
                    onHealthMetricClick = { metricId ->
                        when (metricId) {
                            "heart_rate" -> showHeartRateScreen = true
                            "blood_pressure" -> showBloodPressureScreen = true
                            "glucose" -> showGlucoseScreen = true
                        }
                    }
                )
                selectedTabIndex == 1 -> MedicationsScreen(
                    onAddClick = { showAddMedicine = true },
                    onEditClick = { medication ->
                        editingMedication = medication
                        showAddMedicine = true
                    }
                )
                selectedTabIndex == 2 -> CalendarScreen()
                selectedTabIndex == 3 -> ContactsScreen()
            }
        }
    }
}