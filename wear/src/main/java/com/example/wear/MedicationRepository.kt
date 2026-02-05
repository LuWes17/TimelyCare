package com.example.wear

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.android.gms.wearable.*
import com.example.wear.reminder.ReminderScheduler

data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val medicationTimes: List<String>,
    val frequency: String,
    val isMaintenanceMed: Boolean = false
)

data class MedicineReminder(
    val id: String,
    val medicineName: String,
    val dosage: String,
    val scheduledTime: String,
    val isActive: Boolean = true
)

data class MedicationTakenRecord(
    val medicationId: String,
    val takenDate: String, // Format: "yyyy-MM-dd"
    val takenTime: String, // Format: "HH:mm" - actual time taken
    val scheduledTime: String // Format: "HH:mm" - originally scheduled time
)

data class EmergencyContact(
    val id: String,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val isPrimary: Boolean = false
)

class MedicationRepository private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val prefs: SharedPreferences = context.getSharedPreferences("medications", Context.MODE_PRIVATE)

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications

    private val _takenRecords = MutableStateFlow<List<MedicationTakenRecord>>(emptyList())
    val takenRecords: StateFlow<List<MedicationTakenRecord>> = _takenRecords

    private val _emergencyContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts

    private val _activeReminder = MutableStateFlow<MedicineReminder?>(null)
    val activeReminder: StateFlow<MedicineReminder?> = _activeReminder

    private val dataListener = DataClient.OnDataChangedListener { dataEvents ->
        Log.d("MedicationRepo", "Data changed callback received with ${dataEvents.count} events")
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val item = event.dataItem
                Log.d("MedicationRepo", "Data changed: ${item.uri.path}")

                when (item.uri.path) {
                    "/medication_data" -> {
                        val dataMap = DataMapItem.fromDataItem(item).dataMap
                        val medicationsData = dataMap.getString("medications") ?: ""
                        Log.d("MedicationRepo", "Received medications: $medicationsData")

                        if (medicationsData.isNotEmpty()) {
                            val medications = parseMedicationsFromJson(medicationsData)
                            updateMedications(medications)
                        }
                    }
                }
            }
        }
    }

    init {
        Log.d("MedicationRepo", "Repository init started")

        // Load existing data from preferences
        loadMedications()
        Log.d("MedicationRepo", "Loaded medications from prefs")

        loadTakenRecords()
        Log.d("MedicationRepo", "Loaded taken records from prefs")

        loadEmergencyContacts()
        Log.d("MedicationRepo", "Loaded emergency contacts from prefs")

        // Register data listener
        try {
            Wearable.getDataClient(appContext).addListener(dataListener)
            Log.d("MedicationRepo", "Data listener registered")
        } catch (e: Exception) {
            Log.e("MedicationRepo", "Failed to register data listener", e)
        }

        // Poll for data every 5 seconds as a backup using coroutine
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MedicationRepo", "Data polling coroutine started")
            while (true) {
                try {
                    pollForData()
                } catch (e: Exception) {
                    Log.e("MedicationRepo", "Polling error", e)
                }
                delay(5000)
            }
        }
        Log.d("MedicationRepo", "Repository init completed")
    }

    private fun pollForData() {
        Log.d("MedicationRepo", "Polling for data...")
        Wearable.getDataClient(appContext).dataItems.addOnSuccessListener { dataItems ->
            Log.d("MedicationRepo", "Poll successful - found ${dataItems.count} data items")
            if (dataItems.count > 0) {
                Log.d("MedicationRepo", "Processing ${dataItems.count} data items")
            }

            dataItems.forEach { item ->
                when (item.uri.path) {
                    "/medication_data" -> {
                        val dataMap = DataMapItem.fromDataItem(item).dataMap
                        val medicationsData = dataMap.getString("medications") ?: ""
                        val timestamp = dataMap.getLong("timestamp", 0)

                        // Check if this is new data
                        val lastTimestamp = prefs.getLong("last_medication_sync", 0)
                        if (timestamp > lastTimestamp) {
                            Log.d("MedicationRepo", "Found NEW medication data via polling: $medicationsData")

                            if (medicationsData.isNotEmpty()) {
                                val medications = parseMedicationsFromJson(medicationsData)
                                updateMedications(medications)
                                prefs.edit().putLong("last_medication_sync", timestamp).apply()
                            } else {
                                // Empty data means all medications were deleted
                                Log.d("MedicationRepo", "Clearing all medications")
                                updateMedications(emptyList())
                                prefs.edit().putLong("last_medication_sync", timestamp).apply()
                            }
                        }
                    }
                    "/taken_records" -> {
                        val dataMap = DataMapItem.fromDataItem(item).dataMap
                        val takenRecordsData = dataMap.getString("taken_records") ?: ""
                        val timestamp = dataMap.getLong("timestamp", 0)

                        val lastTimestamp = prefs.getLong("last_taken_sync", 0)
                        if (timestamp > lastTimestamp) {
                            Log.d("MedicationRepo", "Found NEW taken records via polling")
                            val takenRecords = parseTakenRecordsFromJson(takenRecordsData)
                            updateTakenRecords(takenRecords)
                            prefs.edit().putLong("last_taken_sync", timestamp).apply()
                        }
                    }
                    "/emergency_contacts" -> {
                        val dataMap = DataMapItem.fromDataItem(item).dataMap
                        val contactsData = dataMap.getString("emergency_contacts") ?: ""
                        val timestamp = dataMap.getLong("timestamp", 0)

                        val lastTimestamp = prefs.getLong("last_contacts_sync", 0)
                        if (timestamp > lastTimestamp) {
                            Log.d("MedicationRepo", "Found NEW emergency contacts via polling")
                            val contacts = parseEmergencyContactsFromJson(contactsData)
                            updateEmergencyContacts(contacts)
                            prefs.edit().putLong("last_contacts_sync", timestamp).apply()
                        }
                    }
                }
            }
            dataItems.release()
        }.addOnFailureListener { e ->
            Log.e("MedicationRepo", "Polling failed: ${e.message}")
        }
    }

    private fun parseTakenRecordsFromJson(data: String): List<MedicationTakenRecord> {
        return parseTakenRecords(data)
    }

    private fun parseEmergencyContactsFromJson(data: String): List<EmergencyContact> {
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

    fun updateMedications(medications: List<Medication>) {
        android.util.Log.d("WearMedicationRepo", "Updating medications: ${medications.size} items")
        _medications.value = medications
        saveMedications(medications)

        // Schedule reminders for all medications
        ReminderScheduler(appContext).scheduleReminders(medications)
    }

    fun updateTakenRecords(takenRecords: List<MedicationTakenRecord>) {
        android.util.Log.d("WearMedicationRepo", "Updating taken records: ${takenRecords.size} items")
        _takenRecords.value = takenRecords
        saveTakenRecords(takenRecords)
    }

    fun isMedicationTaken(medicationId: String, scheduledTime: String, date: String): Boolean {
        return _takenRecords.value.any { record ->
            record.medicationId == medicationId &&
            record.scheduledTime == scheduledTime &&
            record.takenDate == date
        }
    }

    fun updateEmergencyContacts(contacts: List<EmergencyContact>) {
        android.util.Log.d("WearMedicationRepo", "Updating emergency contacts: ${contacts.size} items")
        _emergencyContacts.value = contacts
        saveEmergencyContacts(contacts)
    }

    fun getPrimaryContact(): EmergencyContact? {
        return _emergencyContacts.value.find { it.isPrimary }
    }

    fun getBackupContacts(): List<EmergencyContact> {
        return _emergencyContacts.value.filter { !it.isPrimary }
    }

    fun triggerReminder(reminder: MedicineReminder) {
        android.util.Log.d("WearMedicationRepo", "Triggering reminder for ${reminder.medicineName}")
        _activeReminder.value = reminder
    }

    fun dismissReminder() {
        android.util.Log.d("WearMedicationRepo", "Dismissing active reminder")
        _activeReminder.value = null
    }

    fun confirmReminder() {
        android.util.Log.d("WearMedicationRepo", "Confirming active reminder")
        _activeReminder.value = null
    }


    companion object {
        @Volatile
        private var INSTANCE: MedicationRepository? = null

        fun getInstance(context: Context): MedicationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedicationRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private fun saveMedications(medications: List<Medication>) {
        val medicationsString = medications.joinToString("|") { med ->
            val timesString = med.medicationTimes.joinToString(";")
            "${med.id},${med.name},${med.dosage},$timesString,${med.frequency},${med.isMaintenanceMed}"
        }
        prefs.edit().putString("medications_data", medicationsString).apply()
    }

    private fun loadMedications() {
        val medicationsString = prefs.getString("medications_data", "") ?: ""
        val medications = parseMedications(medicationsString)
        _medications.value = medications
    }

    private fun parseMedications(data: String): List<Medication> {
        if (data.isEmpty()) return emptyList()

        return data.split("|").mapNotNull { medString ->
            val parts = medString.split(",")
            when (parts.size) {
                5 -> {
                    // Legacy format without maintenance field
                    // Parse times: split by semicolon for multiple times, or use single time
                    val medicationTimes = if (parts[3].contains(";")) {
                        parts[3].split(";")
                    } else {
                        listOf(parts[3])
                    }
                    Medication(parts[0], parts[1], parts[2], medicationTimes, parts[4], false)
                }
                6 -> {
                    // New format with maintenance field
                    val medicationTimes = if (parts[3].contains(";")) {
                        parts[3].split(";")
                    } else {
                        listOf(parts[3])
                    }
                    val isMaintenanceMed = parts[5].toBooleanStrictOrNull() ?: false
                    Medication(parts[0], parts[1], parts[2], medicationTimes, parts[4], isMaintenanceMed)
                }
                else -> null
            }
        }
    }

    private fun parseMedicationsFromJson(data: String): List<Medication> {
        return parseMedications(data)
    }

    private fun saveTakenRecords(takenRecords: List<MedicationTakenRecord>) {
        val recordsString = takenRecords.joinToString("|") { record ->
            "${record.medicationId},${record.takenDate},${record.takenTime},${record.scheduledTime}"
        }
        prefs.edit().putString("taken_records_data", recordsString).apply()
    }

    private fun loadTakenRecords() {
        val recordsString = prefs.getString("taken_records_data", "") ?: ""
        val takenRecords = parseTakenRecords(recordsString)
        _takenRecords.value = takenRecords
    }

    private fun parseTakenRecords(data: String): List<MedicationTakenRecord> {
        if (data.isEmpty()) return emptyList()

        return data.split("|").mapNotNull { recordString ->
            val parts = recordString.split(",")
            when (parts.size) {
                3 -> {
                    // Legacy format: medicationId, takenDate, scheduledTime
                    MedicationTakenRecord(parts[0], parts[1], parts[2], parts[2])
                }
                4 -> {
                    // New format: medicationId, takenDate, takenTime, scheduledTime
                    MedicationTakenRecord(parts[0], parts[1], parts[2], parts[3])
                }
                else -> null
            }
        }
    }


    private fun saveEmergencyContacts(contacts: List<EmergencyContact>) {
        val contactsString = contacts.joinToString("|") { contact ->
            "${contact.id},${contact.name},${contact.relationship},${contact.phoneNumber},${contact.isPrimary}"
        }
        prefs.edit().putString("emergency_contacts_data", contactsString).apply()
    }

    private fun loadEmergencyContacts() {
        val contactsString = prefs.getString("emergency_contacts_data", "") ?: ""
        val contacts = parseEmergencyContacts(contactsString)
        _emergencyContacts.value = contacts
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

}