package com.example.timelycare.service

import com.google.android.gms.wearable.*
import kotlinx.coroutines.tasks.await
import com.example.timelycare.data.Medication
import com.example.timelycare.data.MedicationTakenRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicationDataService {

    companion object {
        private const val MEDICATION_PATH = "/medication_data"
        private const val MEDICATION_KEY = "medications"
        private const val TAKEN_RECORDS_PATH = "/taken_records"
        private const val TAKEN_RECORDS_KEY = "taken_records"
    }

    suspend fun sendMedicationsToWatch(
        dataClient: DataClient,
        medications: List<Medication>
    ) {
        val putDataReq = PutDataMapRequest.create(MEDICATION_PATH).apply {
            dataMap.putString(MEDICATION_KEY, medicationsToJson(medications))
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest()

        dataClient.putDataItem(putDataReq).await()
    }

    suspend fun sendTakenRecordsToWatch(
        dataClient: DataClient,
        takenRecords: List<MedicationTakenRecord>
    ) {
        val putDataReq = PutDataMapRequest.create(TAKEN_RECORDS_PATH).apply {
            dataMap.putString(TAKEN_RECORDS_KEY, takenRecordsToJson(takenRecords))
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest()

        dataClient.putDataItem(putDataReq).await()
    }

    private fun medicationsToJson(medications: List<Medication>): String {
        // Convert to simplified format for watch communication
        return medications.joinToString("|") { med ->
            val firstTime = med.medicationTimes.firstOrNull()?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "No time"
            "${med.id},${med.name},${med.dosage},$firstTime,${med.frequency},${med.isMaintenanceMed}"
        }
    }

    private fun takenRecordsToJson(takenRecords: List<MedicationTakenRecord>): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        return takenRecords.joinToString("|") { record ->
            "${record.medicationId},${record.takenDate.format(dateFormatter)},${record.takenTime.format(timeFormatter)},${record.scheduledTime.format(timeFormatter)}"
        }
    }
}