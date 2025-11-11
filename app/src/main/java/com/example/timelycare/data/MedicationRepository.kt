package com.example.timelycare.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicationRepository {
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    fun addMedication(medication: Medication) {
        _medications.value = _medications.value + medication
    }

    fun updateMedication(medication: Medication) {
        _medications.value = _medications.value.map {
            if (it.id == medication.id) medication else it
        }
    }

    fun deleteMedication(medicationId: String) {
        _medications.value = _medications.value.filter { it.id != medicationId }
    }

    fun getMedicationById(id: String): Medication? {
        return _medications.value.find { it.id == id }
    }

    companion object {
        @Volatile
        private var INSTANCE: MedicationRepository? = null

        fun getInstance(): MedicationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedicationRepository().also { INSTANCE = it }
            }
        }
    }
}