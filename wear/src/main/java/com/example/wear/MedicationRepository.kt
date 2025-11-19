package com.example.wear

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val time: String,
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
    private val prefs: SharedPreferences = context.getSharedPreferences("medications", Context.MODE_PRIVATE)

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications

    private val _takenRecords = MutableStateFlow<List<MedicationTakenRecord>>(emptyList())
    val takenRecords: StateFlow<List<MedicationTakenRecord>> = _takenRecords

    private val _emergencyContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts

    private val _activeReminder = MutableStateFlow<MedicineReminder?>(null)
    val activeReminder: StateFlow<MedicineReminder?> = _activeReminder

    init {
        // Clear existing data and load fresh sample data
        clearAllData()
        addSampleMedications()
        addSampleEmergencyContacts()
    }

    fun updateMedications(medications: List<Medication>) {
        android.util.Log.d("WearMedicationRepo", "Updating medications: ${medications.size} items")
        _medications.value = medications
        saveMedications(medications)
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

    private fun clearAllData() {
        android.util.Log.d("WearMedicationRepo", "Clearing all existing data")
        prefs.edit().clear().apply()
        _medications.value = emptyList()
        _takenRecords.value = emptyList()
        _emergencyContacts.value = emptyList()
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
            "${med.id},${med.name},${med.dosage},${med.time},${med.frequency},${med.isMaintenanceMed}"
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
                    Medication(parts[0], parts[1], parts[2], parts[3], parts[4], false)
                }
                6 -> {
                    // New format with maintenance field
                    val isMaintenanceMed = parts[5].toBooleanStrictOrNull() ?: false
                    Medication(parts[0], parts[1], parts[2], parts[3], parts[4], isMaintenanceMed)
                }
                else -> null
            }
        }
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

    private fun addSampleMedications() {
        val sampleMedications = listOf(
            Medication(
                id = "sample_1",
                name = "Biogesic",
                dosage = "50mg",
                time = "8:00 AM",
                frequency = "Daily",
                isMaintenanceMed = true
            )
        )

        _medications.value = sampleMedications
        saveMedications(sampleMedications)

        // Also add a sample taken record for one medication to show in history
        val sampleTakenRecord = MedicationTakenRecord(
            medicationId = "sample_1",
            takenDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            takenTime = "08:05", // Taken at 8:05 AM
            scheduledTime = "08:00" // Scheduled for 8:00 AM
        )

        _takenRecords.value = listOf(sampleTakenRecord)
        saveTakenRecords(listOf(sampleTakenRecord))

        android.util.Log.d("WearMedicationRepo", "Added ${sampleMedications.size} sample medications")
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

    private fun addSampleEmergencyContacts() {
        val sampleContacts = listOf(
            EmergencyContact(
                id = "contact_1",
                name = "Juan Dela Cruz",
                relationship = "Father",
                phoneNumber = "+1-555-0123",
                isPrimary = true
            )
        )

        _emergencyContacts.value = sampleContacts
        saveEmergencyContacts(sampleContacts)

        android.util.Log.d("WearMedicationRepo", "Added ${sampleContacts.size} sample emergency contacts")
    }
}