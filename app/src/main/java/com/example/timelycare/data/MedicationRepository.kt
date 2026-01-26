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
        loadSampleMedicationsIfEmpty()
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
    private fun loadSampleMedicationsIfEmpty() {
        if (_medications.value.isNotEmpty()) return

        _medications.value = listOf(

            Medication(
                name = "Amlodipine Besylate",
                dosage = "5 mg",
                type = MedicationType.TABLET,
                frequency = Frequency.Daily,
                medicationTimes = listOf(
                    LocalTime.of(8, 0)
                ),
                startDate = LocalDate.now().minusMonths(6),
                endDate = null,
                specialInstructions = "Take in the morning",
                isMaintenanceMed = true
            ),

            Medication(
                name = "Metformin Hydrochloride",
                dosage = "850 mg",
                type = MedicationType.TABLET,
                frequency = Frequency.Daily,
                medicationTimes = listOf(
                    LocalTime.of(7, 0),
                    LocalTime.of(19, 0)
                ),
                startDate = LocalDate.now().minusYears(1),
                endDate = null,
                specialInstructions = "Take with meals",
                isMaintenanceMed = true
            ),

            Medication(
                name = "Atorvastatin Calcium",
                dosage = "20 mg",
                type = MedicationType.TABLET,
                frequency = Frequency.Daily,
                medicationTimes = listOf(
                    LocalTime.of(21, 0)
                ),
                startDate = LocalDate.now().minusMonths(8),
                endDate = null,
                specialInstructions = "Take at night",
                isMaintenanceMed = true
            ),

            Medication(
                name = "Losartan Potassium",
                dosage = "50 mg",
                type = MedicationType.TABLET,
                frequency = Frequency.Daily,
                medicationTimes = listOf(
                    LocalTime.of(8, 0)
                ),
                startDate = LocalDate.now().minusMonths(10),
                endDate = null,
                specialInstructions = "For blood pressure control",
                isMaintenanceMed = true
            ),

            Medication(
                name = "Calcium Carbonate with Vitamin D3",
                dosage = "600 mg / 400 IU",
                type = MedicationType.TABLET,
                frequency = Frequency.Daily,
                medicationTimes = listOf(
                    LocalTime.of(12, 0)
                ),
                startDate = LocalDate.now().minusMonths(3),
                endDate = null,
                specialInstructions = "Take after lunch",
                isMaintenanceMed = true
            ),

            Medication(
                name = "Paracetamol",
                dosage = "500 mg",
                type = MedicationType.PILL,
                frequency = Frequency.Daily,
                medicationTimes = listOf(
                    LocalTime.of(10, 0),
                    LocalTime.of(16, 0)
                ),
                startDate = LocalDate.now().minusDays(3),
                endDate = LocalDate.now().plusDays(5),
                specialInstructions = "As needed for pain",
                isMaintenanceMed = false
            )
        )

        syncToWatch()
    }
    private fun syncToWatch() {
        context?.let { ctx ->
            scope.launch {
                try {
                    val dataClient = Wearable.getDataClient(ctx)

                    medicationDataService.sendMedicationsToWatch(
                        dataClient,
                        _medications.value
                    )

                    takenRepository?.let { repository ->
                        medicationDataService.sendTakenRecordsToWatch(
                            dataClient,
                            repository.takenRecords.value
                        )
                    }

                    Log.d(
                        "MedicationSync",
                        "Synced ${_medications.value.size} medications to watch"
                    )
                } catch (e: Exception) {
                    Log.w(
                        "MedicationSync",
                        "Wear OS sync not available: ${e.message}"
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
