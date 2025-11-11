package com.example.wear

import com.google.android.gms.wearable.*

class WearDataListenerService : WearableListenerService() {

    companion object {
        private const val MEDICATION_PATH = "/medication_data"
        private const val MEDICATION_KEY = "medications"
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val item = event.dataItem
                if (item.uri.path?.compareTo(MEDICATION_PATH) == 0) {
                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                    val medicationsData = dataMap.getString(MEDICATION_KEY)

                    val medications = parseMedications(medicationsData ?: "")

                    // Update local storage
                    updateWatchMedications(medications)
                }
            }
        }
    }

    private fun parseMedications(data: String): List<Medication> {
        if (data.isEmpty()) return emptyList()

        return data.split("|").mapNotNull { medString ->
            val parts = medString.split(",")
            if (parts.size == 5) {
                Medication(parts[0], parts[1], parts[2], parts[3], parts[4])
            } else null
        }
    }

    private fun updateWatchMedications(medications: List<Medication>) {
        // Update repository
        val repository = MedicationRepository(this)
        repository.updateMedications(medications)

        // Log for debugging
        medications.forEach { med ->
            android.util.Log.d("WearMeds", "Received: ${med.name} - ${med.dosage}")
        }
    }
}