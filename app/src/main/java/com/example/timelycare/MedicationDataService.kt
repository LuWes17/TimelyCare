package com.example.timelycare

import com.google.android.gms.wearable.*
import kotlinx.coroutines.tasks.await

class MedicationDataService {

    data class Medication(
        val id: String,
        val name: String,
        val dosage: String,
        val time: String,
        val frequency: String
    )

    companion object {
        private const val MEDICATION_PATH = "/medication_data"
        private const val MEDICATION_KEY = "medications"
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

    private fun medicationsToJson(medications: List<Medication>): String {
        // Simple JSON conversion - you can use Gson/Moshi for complex cases
        return medications.joinToString("|") { med ->
            "${med.id},${med.name},${med.dosage},${med.time},${med.frequency}"
        }
    }
}