package com.example.wear.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wear.Medication
import java.text.SimpleDateFormat
import java.util.*

class ReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val prefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "ReminderScheduler"
        private const val EXTRA_MEDICATION_ID = "medication_id"
        private const val EXTRA_MEDICATION_NAME = "medication_name"
        private const val EXTRA_MEDICATION_DOSAGE = "medication_dosage"
        private const val EXTRA_SCHEDULED_TIME = "scheduled_time"
    }

    /**
     * Schedule reminders for all medications for today
     */
    fun scheduleReminders(medications: List<Medication>) {
        Log.d(TAG, "Scheduling reminders for ${medications.size} medications")

        // Cancel all existing reminders first
        cancelAllReminders()

        val calendar = Calendar.getInstance()
        val currentTimeInMillis = calendar.timeInMillis

        // Store request codes for future cancellation
        val requestCodes = mutableSetOf<Int>()

        medications.forEach { medication ->
            medication.medicationTimes.forEachIndexed { index, timeString ->
                try {
                    val scheduledTime = parseTimeToCalendar(timeString)

                    // Only schedule if the time hasn't passed today
                    if (scheduledTime.timeInMillis > currentTimeInMillis) {
                        val requestCode = generateRequestCode(medication.id, index)
                        requestCodes.add(requestCode)

                        scheduleReminderForTime(
                            medication = medication,
                            timeString = timeString,
                            scheduledTime = scheduledTime,
                            requestCode = requestCode
                        )

                        Log.d(TAG, "Scheduled: ${medication.name} at $timeString (code: $requestCode)")
                    } else {
                        Log.d(TAG, "Skipped past time: ${medication.name} at $timeString")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to schedule reminder for ${medication.name} at $timeString", e)
                }
            }
        }

        // Save request codes for future cancellation
        saveRequestCodes(requestCodes)
        Log.d(TAG, "Total reminders scheduled: ${requestCodes.size}")
    }

    /**
     * Schedule a single reminder
     */
    private fun scheduleReminderForTime(
        medication: Medication,
        timeString: String,
        scheduledTime: Calendar,
        requestCode: Int
    ) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_MEDICATION_ID, medication.id)
            putExtra(EXTRA_MEDICATION_NAME, medication.name)
            putExtra(EXTRA_MEDICATION_DOSAGE, medication.dosage)
            putExtra(EXTRA_SCHEDULED_TIME, timeString)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use setExactAndAllowWhileIdle for accurate timing even in doze mode
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            scheduledTime.timeInMillis,
            pendingIntent
        )
    }

    /**
     * Cancel all scheduled reminders
     */
    fun cancelAllReminders() {
        val requestCodes = loadRequestCodes()
        Log.d(TAG, "Canceling ${requestCodes.size} reminders")

        requestCodes.forEach { requestCode ->
            val intent = Intent(context, ReminderBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        // Clear stored request codes
        prefs.edit().remove("request_codes").apply()
    }

    /**
     * Parse time string (e.g., "8:00 AM") to Calendar object for today
     */
    private fun parseTimeToCalendar(timeString: String): Calendar {
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        val time = formatter.parse(timeString)
            ?: throw IllegalArgumentException("Invalid time format: $timeString")

        val calendar = Calendar.getInstance().apply {
            val parsedCalendar = Calendar.getInstance().apply { setTime(time) }
            set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return calendar
    }

    /**
     * Generate unique request code for each medication time
     */
    private fun generateRequestCode(medicationId: String, index: Int): Int {
        return "${medicationId}_$index".hashCode()
    }

    /**
     * Save request codes to SharedPreferences
     */
    private fun saveRequestCodes(requestCodes: Set<Int>) {
        val codesString = requestCodes.joinToString(",")
        prefs.edit().putString("request_codes", codesString).apply()
    }

    /**
     * Load request codes from SharedPreferences
     */
    private fun loadRequestCodes(): Set<Int> {
        val codesString = prefs.getString("request_codes", "") ?: ""
        return if (codesString.isEmpty()) {
            emptySet()
        } else {
            codesString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        }
    }
}
