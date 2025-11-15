package com.example.timelycare.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MedicationTakenRepository private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "medication_taken_records",
        Context.MODE_PRIVATE
    )

    private val _takenRecords = MutableStateFlow<List<MedicationTakenRecord>>(emptyList())
    val takenRecords: StateFlow<List<MedicationTakenRecord>> = _takenRecords.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    init {
        loadTakenRecords()
    }

    fun markAsTaken(medicationId: String, scheduledTime: LocalTime) {
        val now = LocalTime.now()
        val today = LocalDate.now()

        val takenRecord = MedicationTakenRecord(
            medicationId = medicationId,
            takenDate = today,
            takenTime = now,
            scheduledTime = scheduledTime
        )

        val currentRecords = _takenRecords.value.toMutableList()

        // Remove any existing record for the same medication on the same date with same scheduled time
        currentRecords.removeAll { record ->
            record.medicationId == medicationId &&
            record.takenDate == today &&
            record.scheduledTime == scheduledTime
        }

        currentRecords.add(takenRecord)
        _takenRecords.value = currentRecords

        saveTakenRecords()
        triggerSync()
        Log.d("MedicationTaken", "Marked medication $medicationId as taken at $now")
    }

    fun markAsNotTaken(medicationId: String, scheduledTime: LocalTime, date: LocalDate = LocalDate.now()) {
        val currentRecords = _takenRecords.value.toMutableList()

        currentRecords.removeAll { record ->
            record.medicationId == medicationId &&
            record.takenDate == date &&
            record.scheduledTime == scheduledTime
        }

        _takenRecords.value = currentRecords
        saveTakenRecords()
        triggerSync()
        Log.d("MedicationTaken", "Marked medication $medicationId as not taken for $date at $scheduledTime")
    }

    fun isMedicationTaken(medicationId: String, scheduledTime: LocalTime, date: LocalDate = LocalDate.now()): Boolean {
        return _takenRecords.value.any { record ->
            record.medicationId == medicationId &&
            record.takenDate == date &&
            record.scheduledTime == scheduledTime
        }
    }

    fun getTakenRecordsForDate(date: LocalDate): List<MedicationTakenRecord> {
        return _takenRecords.value.filter { record ->
            record.takenDate == date
        }
    }

    fun getTakenRecordsForMedication(medicationId: String): List<MedicationTakenRecord> {
        return _takenRecords.value.filter { record ->
            record.medicationId == medicationId
        }
    }

    private fun saveTakenRecords() {
        val recordsString = _takenRecords.value.joinToString("|") { record ->
            "${record.recordId},${record.medicationId},${record.takenDate.format(dateFormatter)},${record.takenTime.format(timeFormatter)},${record.scheduledTime.format(timeFormatter)}"
        }

        prefs.edit()
            .putString("taken_records", recordsString)
            .apply()
    }

    private fun loadTakenRecords() {
        val recordsString = prefs.getString("taken_records", "") ?: ""
        val records = parseTakenRecords(recordsString)
        _takenRecords.value = records
    }

    private fun parseTakenRecords(data: String): List<MedicationTakenRecord> {
        if (data.isEmpty()) return emptyList()

        return data.split("|").mapNotNull { recordString ->
            try {
                val parts = recordString.split(",")
                if (parts.size == 5) {
                    MedicationTakenRecord(
                        recordId = parts[0],
                        medicationId = parts[1],
                        takenDate = LocalDate.parse(parts[2], dateFormatter),
                        takenTime = LocalTime.parse(parts[3], timeFormatter),
                        scheduledTime = LocalTime.parse(parts[4], timeFormatter)
                    )
                } else null
            } catch (e: Exception) {
                Log.w("MedicationTaken", "Failed to parse taken record: $recordString", e)
                null
            }
        }
    }

    private fun triggerSync() {
        try {
            // Get MedicationRepository instance and trigger sync
            MedicationRepository.getInstance(appContext).syncTakenStatusToWatch()
        } catch (e: Exception) {
            Log.w("MedicationTaken", "Failed to trigger sync to watch: ${e.message}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MedicationTakenRepository? = null

        fun getInstance(context: Context): MedicationTakenRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedicationTakenRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}