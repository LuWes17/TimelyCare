package com.example.timelycare.service

import com.google.android.gms.wearable.*
import kotlinx.coroutines.tasks.await
import com.example.timelycare.data.Medication
import com.example.timelycare.data.MedicationTakenRecord
import com.example.timelycare.data.EmergencyContact
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicationDataService {

    companion object {
        private const val MEDICATION_PATH = "/medication_data"
        private const val MEDICATION_KEY = "medications"
        private const val TAKEN_RECORDS_PATH = "/taken_records"
        private const val TAKEN_RECORDS_KEY = "taken_records"
        private const val EMERGENCY_CONTACTS_PATH = "/emergency_contacts"
        private const val EMERGENCY_CONTACTS_KEY = "emergency_contacts"
    }

    suspend fun sendMedicationsToWatch(
        dataClient: DataClient,
        medications: List<Medication>
    ) {
        val jsonData = medicationsToJson(medications)
        android.util.Log.d("MedicationDataService", "Sending medications JSON: $jsonData")

        val putDataReq = PutDataMapRequest.create(MEDICATION_PATH).apply {
            dataMap.putString(MEDICATION_KEY, jsonData)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        val result = dataClient.putDataItem(putDataReq).await()
        android.util.Log.d("MedicationDataService", "Data sent. URI: ${result.uri}")
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
            // Send all medication times as semicolon-separated string
            val allTimes = med.medicationTimes.joinToString(";") { time ->
                time.format(DateTimeFormatter.ofPattern("h:mm a"))
            }
            val timesString = if (allTimes.isEmpty()) "No time" else allTimes
            "${med.id},${med.name},${med.dosage},$timesString,${med.frequency},${med.isMaintenanceMed}"
        }
    }

    private fun takenRecordsToJson(takenRecords: List<MedicationTakenRecord>): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        return takenRecords.joinToString("|") { record ->
            "${record.medicationId},${record.takenDate.format(dateFormatter)},${record.takenTime.format(timeFormatter)},${record.scheduledTime.format(timeFormatter)}"
        }
    }

    suspend fun sendEmergencyContactsToWatch(
        dataClient: DataClient,
        contacts: List<EmergencyContact>
    ) {
        val putDataReq = PutDataMapRequest.create(EMERGENCY_CONTACTS_PATH).apply {
            dataMap.putString(EMERGENCY_CONTACTS_KEY, emergencyContactsToJson(contacts))
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest()

        dataClient.putDataItem(putDataReq).await()
    }

    private fun emergencyContactsToJson(contacts: List<EmergencyContact>): String {
        // Mark the first contact as primary
        return contacts.mapIndexed { index, contact ->
            val isPrimary = index == 0
            val phoneNumber = "${contact.countryCode}${contact.phone}"
            "${contact.id},${contact.name},${contact.relationship},$phoneNumber,$isPrimary"
        }.joinToString("|")
    }
}