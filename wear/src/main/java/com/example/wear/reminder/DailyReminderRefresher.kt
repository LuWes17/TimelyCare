package com.example.wear.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wear.MedicationRepository
import java.util.*

class DailyReminderRefresher : BroadcastReceiver() {

    companion object {
        private const val TAG = "DailyRefresher"
        private const val REFRESH_REQUEST_CODE = 999999

        /**
         * Schedule the daily refresh to run at 12:01 AM every day
         */
        fun scheduleDailyRefresh(context: Context) {
            Log.d(TAG, "Setting up daily reminder refresh")

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, DailyReminderRefresher::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REFRESH_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Set the time to 12:01 AM tomorrow
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // If 12:01 AM has already passed today, schedule for tomorrow
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            // Schedule repeating alarm
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

            Log.d(TAG, "Daily refresh scheduled for ${calendar.time}")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Daily refresh triggered - rescheduling all reminders for today")

        // Get medications from repository
        val repository = MedicationRepository.getInstance(context)
        val medications = repository.medications.value

        // Reschedule all reminders for the new day
        val scheduler = ReminderScheduler(context)
        scheduler.scheduleReminders(medications)

        Log.d(TAG, "All reminders rescheduled for today")
    }
}
