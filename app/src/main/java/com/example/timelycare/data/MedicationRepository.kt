package com.example.timelycare.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.gms.wearable.Wearable
import com.example.timelycare.service.MedicationDataService

class MedicationRepository private constructor(private val context: Context?) {
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    private val medicationDataService = MedicationDataService()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val takenRepository: MedicationTakenRepository? by lazy {
        context?.let { MedicationTakenRepository.getInstance(it) }
    }

    fun addMedication(medication: Medication) {
        _medications.value = _medications.value + medication
        syncToWatch()
    }

    fun updateMedication(medication: Medication) {
        _medications.value = _medications.value.map {
            if (it.id == medication.id) medication else it
        }
        syncToWatch()
    }

    fun deleteMedication(medicationId: String) {
        _medications.value = _medications.value.filter { it.id != medicationId }
        syncToWatch()
    }

    private fun syncToWatch() {
        context?.let { ctx ->
            scope.launch {
                try {
                    val dataClient = Wearable.getDataClient(ctx)

                    // Sync medications
                    medicationDataService.sendMedicationsToWatch(dataClient, _medications.value)

                    // Sync taken records
                    takenRepository?.let { repository ->
                        medicationDataService.sendTakenRecordsToWatch(dataClient, repository.takenRecords.value)
                    }

                    Log.d("MedicationSync", "Successfully synced ${_medications.value.size} medications and taken records to watch")
                } catch (e: Exception) {
                    Log.w("MedicationSync", "Wear OS sync not available: ${e.message}")
                    // Could implement alternative sync method here (e.g., shared preferences, file system, etc.)
                }
            }
        }
    }

    fun getMedicationById(id: String): Medication? {
        return _medications.value.find { it.id == id }
    }

    fun syncTakenStatusToWatch() {
        syncToWatch()
    }

    companion object {
        @Volatile
        private var INSTANCE: MedicationRepository? = null

        fun getInstance(context: Context? = null): MedicationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedicationRepository(context).also { INSTANCE = it }
            }
        }
    }
}