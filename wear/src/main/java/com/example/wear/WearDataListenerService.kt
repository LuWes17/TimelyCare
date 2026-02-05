package com.example.wear

import com.google.android.gms.wearable.*

class WearDataListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "WearDataListener"
        private const val MEDICATION_PATH = "/medication_data"
        private const val MEDICATION_KEY = "medications"
        private const val TAKEN_RECORDS_PATH = "/taken_records"
        private const val TAKEN_RECORDS_KEY = "taken_records"
        private const val EMERGENCY_CONTACTS_PATH = "/emergency_contacts"
        private const val EMERGENCY_CONTACTS_KEY = "emergency_contacts"
    }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d(TAG, "WearDataListenerService created")
    }

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.d(TAG, "WearDataListenerService destroyed")
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        android.util.Log.d(TAG, "================================================")
        android.util.Log.d(TAG, "onDataChanged called with ${dataEvents.count} events")
        android.util.Log.d(TAG, "================================================")

        dataEvents.forEach { event ->
            android.util.Log.d(TAG, "Processing event type: ${event.type}")
            if (event.type == DataEvent.TYPE_CHANGED) {
                val item = event.dataItem
                android.util.Log.d(TAG, "Data item URI: ${item.uri}")
                android.util.Log.d(TAG, "Data item path: ${item.uri.path}")
                when (item.uri.path) {
                    MEDICATION_PATH -> {
                        val dataMap = DataMapItem.fromDataItem(item).dataMap
                        val medicationsData = dataMap.getString(MEDICATION_KEY)
                        android.util.Log.d("WearDataListener", "Received medication data: $medicationsData")

                        val medications = parseMedications(medicationsData ?: "")
                        android.util.Log.d("WearDataListener", "Parsed ${medications.size} medications")

                        // Update local storage
                        updateWatchMedications(medications)
                    }
                    TAKEN_RECORDS_PATH -> {
                        val dataMap = DataMapItem.fromDataItem(item).dataMap
                        val takenRecordsData = dataMap.getString(TAKEN_RECORDS_KEY)
                        android.util.Log.d("WearDataListener", "Received taken records data: $takenRecordsData")

                        val takenRecords = parseTakenRecords(takenRecordsData ?: "")
                        android.util.Log.d("WearDataListener", "Parsed ${takenRecords.size} taken records")

                        // Update local storage
                        updateWatchTakenRecords(takenRecords)
                    }
                    EMERGENCY_CONTACTS_PATH -> {
                        val dataMap = DataMapItem.fromDataItem(item).dataMap
                        val contactsData = dataMap.getString(EMERGENCY_CONTACTS_KEY)
                        android.util.Log.d("WearDataListener", "Received emergency contacts data: $contactsData")

                        val contacts = parseEmergencyContacts(contactsData ?: "")
                        android.util.Log.d("WearDataListener", "Parsed ${contacts.size} emergency contacts")

                        // Update local storage
                        updateWatchEmergencyContacts(contacts)
                    }
                    else -> {
                        android.util.Log.d("WearDataListener", "Unknown path: ${item.uri.path}")
                    }
                }
            }
        }
    }

    private fun parseMedications(data: String): List<Medication> {
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
                Medication(
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

    private fun updateWatchMedications(medications: List<Medication>) {
        // Update repository using singleton
        val repository = MedicationRepository.getInstance(this)
        repository.updateMedications(medications)

        android.util.Log.d("WearDataListener", "Updated repository with ${medications.size} medications")

        // Log for debugging
        medications.forEach { med ->
            android.util.Log.d("WearMeds", "Received: ${med.name} - ${med.dosage}")
        }
    }

    private fun parseTakenRecords(data: String): List<MedicationTakenRecord> {
        if (data.isEmpty()) return emptyList()

        return data.split("|").mapNotNull { recordString ->
            val parts = recordString.split(",")
            when (parts.size) {
                3 -> {
                    // Legacy format: medicationId, takenDate, scheduledTime
                    MedicationTakenRecord(
                        medicationId = parts[0],
                        takenDate = parts[1],
                        takenTime = parts[2], // Use scheduled time as taken time for legacy
                        scheduledTime = parts[2]
                    )
                }
                4 -> {
                    // New format: medicationId, takenDate, takenTime, scheduledTime
                    MedicationTakenRecord(
                        medicationId = parts[0],
                        takenDate = parts[1],
                        takenTime = parts[2],
                        scheduledTime = parts[3]
                    )
                }
                else -> null
            }
        }
    }

    private fun updateWatchTakenRecords(takenRecords: List<MedicationTakenRecord>) {
        // Update repository using singleton
        val repository = MedicationRepository.getInstance(this)
        repository.updateTakenRecords(takenRecords)

        android.util.Log.d("WearDataListener", "Updated repository with ${takenRecords.size} taken records")

        // Log for debugging
        takenRecords.forEach { record ->
            android.util.Log.d("WearTakenRecords", "Received: ${record.medicationId} - ${record.takenDate} - ${record.scheduledTime}")
        }
    }

    private fun parseEmergencyContacts(data: String): List<EmergencyContact> {
        if (data.isEmpty()) return emptyList()

        return data.split("|").mapNotNull { contactString ->
            val parts = contactString.split(",")
            if (parts.size == 5) {
                EmergencyContact(
                    id = parts[0],
                    name = parts[1],
                    relationship = parts[2],
                    phoneNumber = parts[3],
                    isPrimary = parts[4].toBoolean()
                )
            } else null
        }
    }

    private fun updateWatchEmergencyContacts(contacts: List<EmergencyContact>) {
        // Update repository using singleton
        val repository = MedicationRepository.getInstance(this)
        repository.updateEmergencyContacts(contacts)

        android.util.Log.d("WearDataListener", "Updated repository with ${contacts.size} emergency contacts")

        // Log for debugging
        contacts.forEach { contact ->
            android.util.Log.d("WearEmergencyContacts", "Received: ${contact.name} - ${contact.phoneNumber} - Primary: ${contact.isPrimary}")
        }
    }
}