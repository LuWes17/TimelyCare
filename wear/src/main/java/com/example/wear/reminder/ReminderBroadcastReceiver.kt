package com.example.wear.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wear.MedicationRepository
import com.example.wear.MedicineReminder

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderReceiver"
        private const val EXTRA_MEDICATION_ID = "medication_id"
        private const val EXTRA_MEDICATION_NAME = "medication_name"
        private const val EXTRA_MEDICATION_DOSAGE = "medication_dosage"
        private const val EXTRA_SCHEDULED_TIME = "scheduled_time"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Reminder broadcast received")

        // Extract medication details from intent
        val medicationId = intent.getStringExtra(EXTRA_MEDICATION_ID) ?: return
        val medicationName = intent.getStringExtra(EXTRA_MEDICATION_NAME) ?: return
        val medicationDosage = intent.getStringExtra(EXTRA_MEDICATION_DOSAGE) ?: return
        val scheduledTime = intent.getStringExtra(EXTRA_SCHEDULED_TIME) ?: return

        Log.d(TAG, "Triggering reminder for $medicationName at $scheduledTime")

        // Create reminder object
        val reminder = MedicineReminder(
            id = medicationId,
            medicineName = medicationName,
            dosage = medicationDosage,
            scheduledTime = scheduledTime,
            isActive = true
        )

        // Trigger the reminder via repository
        // This will display the reminder overlay and auto-launch the app if needed
        val repository = MedicationRepository.getInstance(context)
        repository.triggerReminder(reminder)

        Log.d(TAG, "Reminder triggered successfully")
    }
}
