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
import java.time.LocalDate
import java.time.LocalTime

class MedicationRepository private constructor(
    private val context: Context?
) {

    // In-memory medication list
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    private val medicationDataService = MedicationDataService()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val takenRepository: MedicationTakenRepository? by lazy {
        context?.let { MedicationTakenRepository.getInstance(it) }
    }

    init {
        // Start with empty medication list
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

    fun getMedicationById(id: String): Medication? {
        return _medications.value.find { it.id == id }
    }
    private fun syncToWatch() {
        context?.let { ctx ->
            scope.launch {
                try {
                    val dataClient = Wearable.getDataClient(ctx)

                    Log.d("MedicationSync", "Starting sync to watch...")
                    Log.d("MedicationSync", "Medications to sync: ${_medications.value.size}")

                    medicationDataService.sendMedicationsToWatch(
                        dataClient,
                        _medications.value
                    )

                    Log.d("MedicationSync", "Medications sent successfully")

                    takenRepository?.let { repository ->
                        medicationDataService.sendTakenRecordsToWatch(
                            dataClient,
                            repository.takenRecords.value
                        )
                        Log.d("MedicationSync", "Taken records sent successfully")
                    }

                    Log.d(
                        "MedicationSync",
                        "Synced ${_medications.value.size} medications to watch"
                    )
                } catch (e: Exception) {
                    Log.e(
                        "MedicationSync",
                        "Wear OS sync failed: ${e.message}", e
                    )
                }
            }
        }
    }

    fun syncTakenStatusToWatch() {
        syncToWatch()
    }

    companion object {
        @Volatile
        private var INSTANCE: MedicationRepository? = null

        fun getInstance(context: Context? = null): MedicationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedicationRepository(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}
