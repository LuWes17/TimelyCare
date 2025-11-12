package com.example.timelycare.data

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Stable
data class HealthMetric(
    val id: String,
    val title: String,
    val value: String,
    val unit: String = "",
    val iconType: HealthMetricIcon,
    val status: HealthStatus = HealthStatus.NORMAL
)

enum class HealthMetricIcon {
    HEART_RATE,
    BLOOD_PRESSURE,
    GLUCOSE
}

enum class HealthStatus {
    NORMAL,
    ELEVATED,
    HIGH
}

object HealthMetricsData {
    val heartRate = HealthMetric(
        id = "heart_rate",
        title = "Heart Rate",
        value = "72",
        unit = "BPM",
        iconType = HealthMetricIcon.HEART_RATE,
        status = HealthStatus.NORMAL
    )

    val bloodPressure = HealthMetric(
        id = "blood_pressure",
        title = "Blood Pressure",
        value = "120/80",
        unit = "",
        iconType = HealthMetricIcon.BLOOD_PRESSURE,
        status = HealthStatus.NORMAL
    )

    val glucose = HealthMetric(
        id = "glucose",
        title = "Glucose",
        value = "95",
        unit = "mg/dL",
        iconType = HealthMetricIcon.GLUCOSE,
        status = HealthStatus.NORMAL
    )

    val allMetrics = listOf(heartRate, bloodPressure, glucose)
}

class HealthMetricsRepository private constructor() {
    private val _healthMetrics = MutableStateFlow(HealthMetricsData.allMetrics)
    val healthMetrics: StateFlow<List<HealthMetric>> = _healthMetrics.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: HealthMetricsRepository? = null

        fun getInstance(): HealthMetricsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HealthMetricsRepository().also { INSTANCE = it }
            }
        }
    }

    fun getMetricById(id: String): HealthMetric? {
        return _healthMetrics.value.find { it.id == id }
    }

    fun updateMetric(updatedMetric: HealthMetric) {
        val currentMetrics = _healthMetrics.value.toMutableList()
        val index = currentMetrics.indexOfFirst { it.id == updatedMetric.id }
        if (index != -1) {
            currentMetrics[index] = updatedMetric
            _healthMetrics.value = currentMetrics
        }
    }
}