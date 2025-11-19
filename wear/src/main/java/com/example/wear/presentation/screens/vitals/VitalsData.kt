package com.example.wear.presentation.screens.vitals

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import kotlin.random.Random

// Data classes for vitals
data class HeartRateData(
    val bpm: Int,
    val graphPoints: List<Float> = listOf(0.4f, 0.3f, 0.5f, 0.2f, 0.6f, 0.4f, 0.3f)
)

data class BloodPressureData(
    val systolic: Int,
    val diastolic: Int,
    val systolicPoints: List<Float> = listOf(0.3f, 0.2f, 0.4f, 0.3f, 0.5f, 0.4f),
    val diastolicPoints: List<Float> = listOf(0.7f, 0.6f, 0.8f, 0.7f, 0.9f, 0.8f)
)

data class BloodGlucoseData(
    val level: Int,
    val graphPoints: List<Float> = listOf(0.6f, 0.5f, 0.7f, 0.4f, 0.8f, 0.6f, 0.5f)
)

data class VitalsOverview(
    val heartRate: HeartRateData,
    val bloodPressure: BloodPressureData,
    val bloodGlucose: BloodGlucoseData
)

// Repository for managing vitals data with real-time simulation
class VitalsRepository {
    private val _heartRate = MutableStateFlow(HeartRateData(72))
    val heartRate: StateFlow<HeartRateData> = _heartRate.asStateFlow()

    private val _bloodPressure = MutableStateFlow(BloodPressureData(120, 80))
    val bloodPressure: StateFlow<BloodPressureData> = _bloodPressure.asStateFlow()

    private val _bloodGlucose = MutableStateFlow(BloodGlucoseData(95))
    val bloodGlucose: StateFlow<BloodGlucoseData> = _bloodGlucose.asStateFlow()

    private val _vitalsOverview = MutableStateFlow(
        VitalsOverview(
            heartRate = HeartRateData(72),
            bloodPressure = BloodPressureData(120, 80),
            bloodGlucose = BloodGlucoseData(95)
        )
    )
    val vitalsOverview: StateFlow<VitalsOverview> = _vitalsOverview.asStateFlow()

    suspend fun startRealTimeSimulation() {
        while (true) {
            delay(3000) // Update every 3 seconds

            // Simulate realistic heart rate variations (60-100 BPM)
            val newHeartRate = Random.nextInt(65, 85)
            val heartRatePoints = generateGraphPoints()
            _heartRate.value = HeartRateData(newHeartRate, heartRatePoints)

            // Simulate realistic blood pressure variations
            val newSystolic = Random.nextInt(110, 140)
            val newDiastolic = Random.nextInt(70, 90)
            val systolicPoints = generateGraphPoints()
            val diastolicPoints = generateGraphPoints()
            _bloodPressure.value = BloodPressureData(newSystolic, newDiastolic, systolicPoints, diastolicPoints)

            // Simulate realistic blood glucose variations (80-120 mg/dL)
            val newGlucose = Random.nextInt(85, 115)
            val glucosePoints = generateGraphPoints()
            _bloodGlucose.value = BloodGlucoseData(newGlucose, glucosePoints)

            // Update overview
            _vitalsOverview.value = VitalsOverview(
                heartRate = _heartRate.value,
                bloodPressure = _bloodPressure.value,
                bloodGlucose = _bloodGlucose.value
            )
        }
    }

    private fun generateGraphPoints(): List<Float> {
        return (0..6).map { Random.nextFloat() * 0.8f + 0.1f }
    }

    companion object {
        @Volatile
        private var INSTANCE: VitalsRepository? = null

        fun getInstance(): VitalsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VitalsRepository().also { INSTANCE = it }
            }
        }
    }
}

// Composable function to start real-time simulation
@Composable
fun rememberVitalsRepository(): VitalsRepository {
    val repository = remember { VitalsRepository.getInstance() }

    LaunchedEffect(Unit) {
        repository.startRealTimeSimulation()
    }

    return repository
}