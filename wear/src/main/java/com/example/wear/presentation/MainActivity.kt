/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.wear.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.wear.presentation.theme.TimelyCareTheme
import com.example.wear.presentation.navigation.AppNavigation
import com.example.wear.WearDataListenerService
import com.google.android.gms.wearable.Wearable
import com.example.wear.reminder.ReminderScheduler
import com.example.wear.reminder.DailyReminderRefresher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        // Poll for existing data items and manually trigger the listener service
        Log.d("MainActivity", "Checking for existing data from phone...")
        Wearable.getDataClient(this).dataItems.addOnSuccessListener { dataItems ->
            Log.d("MainActivity", "Found ${dataItems.count} existing data items")

            // Manually process existing data items since WearableListenerService might not have been triggered yet
            val repository = com.example.wear.MedicationRepository.getInstance(this)
            dataItems.forEach { item ->
                Log.d("MainActivity", "Processing existing data item: ${item.uri.path}")

                when (item.uri.path) {
                    "/medication_data" -> {
                        val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(item).dataMap
                        val medicationsData = dataMap.getString("medications")
                        Log.d("MainActivity", "Found medication data: $medicationsData")

                        if (medicationsData != null && medicationsData.isNotEmpty()) {
                            val medications = parseMedications(medicationsData)
                            Log.d("MainActivity", "Parsed ${medications.size} medications")
                            repository.updateMedications(medications)
                        }
                    }
                    "/taken_records" -> {
                        val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(item).dataMap
                        val takenRecordsData = dataMap.getString("taken_records")
                        Log.d("MainActivity", "Found taken records data: $takenRecordsData")
                    }
                    "/emergency_contacts" -> {
                        val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(item).dataMap
                        val contactsData = dataMap.getString("emergency_contacts")
                        Log.d("MainActivity", "Found emergency contacts data: $contactsData")
                    }
                }
            }
            dataItems.release()

            // Schedule reminders for all loaded medications
            val medications = repository.medications.value
            ReminderScheduler(this).scheduleReminders(medications)
            Log.d("MainActivity", "Scheduled reminders for ${medications.size} medications")

            // Set up daily reminder refresh at midnight
            DailyReminderRefresher.scheduleDailyRefresh(this)
            Log.d("MainActivity", "Daily reminder refresh configured")
        }.addOnFailureListener { e ->
            Log.e("MainActivity", "Failed to get data items", e)
        }

        setContent {
            AppNavigation()
        }
    }

    private fun parseMedications(data: String): List<com.example.wear.Medication> {
        if (data.isEmpty()) return emptyList()

        return data.split("|").mapNotNull { medString ->
            val parts = medString.split(",")
            if (parts.size == 6) {
                // Parse times: split by semicolon for multiple times, or use single time
                val medicationTimes = if (parts[3].contains(";")) {
                    parts[3].split(";")
                } else {
                    listOf(parts[3])
                }
                com.example.wear.Medication(
                    id = parts[0],
                    name = parts[1],
                    dosage = parts[2],
                    medicationTimes = medicationTimes,
                    frequency = parts[4],
                    isMaintenanceMed = parts[5].toBoolean()
                )
            } else null
        }
    }
}

