package com.example.timelycare.data

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Stable
data class HeartRateReading(
    val bpm: Int,
    val timestamp: String,
    val zone: HeartRateZone
) {
    val zoneColor: HeartRateZoneColor
        get() = when (zone) {
            HeartRateZone.NORMAL -> HeartRateZoneColor.GREEN
            HeartRateZone.ELEVATED -> HeartRateZoneColor.YELLOW
            HeartRateZone.HIGH -> HeartRateZoneColor.RED
        }
}

@Stable
data class DailyHeartRateData(
    val date: LocalDate,
    val readings: List<HeartRateReading>,
    val average: Int,
    val min: Int,
    val max: Int,
    val current: Int
)

@Stable
data class WeeklyHeartRateData(
    val weekDays: List<DailySummary>
) {
    @Stable
    data class DailySummary(
        val date: LocalDate,
        val dayName: String,
        val averageBpm: Int
    )
}

@Stable
data class HeartRateZones(
    val restingPercentage: Int,
    val lightActivityPercentage: Int,
    val moderateActivityPercentage: Int,
    val intenseActivityPercentage: Int
)

@Stable
data class HistoricalReading(
    val date: LocalDate,
    val displayDate: String,
    val bpm: Int,
    val zone: HeartRateZone
)

enum class HeartRateZone {
    NORMAL,    // ≤70
    ELEVATED,  // 71–80
    HIGH       // ≥81
}

enum class HeartRateZoneColor {
    GREEN, YELLOW, RED
}

object HeartRateDataGenerator {

    private val FULL_DATE_FORMAT =
        DateTimeFormatter.ofPattern("EEE, MMM d")

    private fun zoneFor(bpm: Int): HeartRateZone =
        when {
            bpm <= 70 -> HeartRateZone.NORMAL
            bpm <= 80 -> HeartRateZone.ELEVATED
            else -> HeartRateZone.HIGH
        }

    fun getCurrentReading(): HeartRateReading {
        val bpm = 72
        return HeartRateReading(
            bpm = bpm,
            timestamp = "Now",
            zone = zoneFor(bpm)
        )
    }

    fun getTodayData(): DailyHeartRateData {
        val hourlyReadings = listOf(
            HeartRateReading(64, "6 AM", zoneFor(64)),
            HeartRateReading(71, "8 AM", zoneFor(71)),
            HeartRateReading(77, "10 AM", zoneFor(77)),
            HeartRateReading(74, "12 PM", zoneFor(74)),
            HeartRateReading(69, "2 PM", zoneFor(69)),
            HeartRateReading(66, "4 PM", zoneFor(66)),
            HeartRateReading(65, "6 PM", zoneFor(65)),
            HeartRateReading(64, "8 PM", zoneFor(64))
        )

        val values = hourlyReadings.map { it.bpm }

        return DailyHeartRateData(
            date = LocalDate.now(),
            readings = hourlyReadings,
            average = values.average().toInt(),
            min = values.minOrNull() ?: 0,
            max = values.maxOrNull() ?: 0,
            current = getCurrentReading().bpm
        )
    }

    fun getWeeklyData(): WeeklyHeartRateData {
        val today = LocalDate.now()

        val bpmSamples = listOf(72, 65, 70, 74, 69, 71, 67)

        val days = (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())

            val dayName = when (date) {
                today -> "Today"
                today.minusDays(1) -> "Yesterday"
                else -> date.format(DateTimeFormatter.ofPattern("EEE"))
            }

            WeeklyHeartRateData.DailySummary(
                date = date,
                dayName = dayName,
                averageBpm = bpmSamples[6 - daysAgo]
            )
        }

        return WeeklyHeartRateData(days)
    }

    fun getHeartRateZones(): HeartRateZones {
        return HeartRateZones(
            restingPercentage = 40,
            lightActivityPercentage = 40,
            moderateActivityPercentage = 15,
            intenseActivityPercentage = 5
        )
    }

    fun getHistoricalReadings(): List<HistoricalReading> {
        val today = LocalDate.now()
        val bpmSamples = listOf(67, 71, 69, 74, 70, 65, 72, 68)

        fun displayLabel(date: LocalDate): String =
            when (date) {
                today -> "Today"
                today.minusDays(1) -> "Yesterday"
                else -> date.format(FULL_DATE_FORMAT)
            }

        return (0..7).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val bpm = bpmSamples[daysAgo]

            HistoricalReading(
                date = date,
                displayDate = displayLabel(date),
                bpm = bpm,
                zone = zoneFor(bpm)
            )
        }
    }
}

class HeartRateRepository private constructor() {

    private val _currentReading =
        MutableStateFlow(HeartRateDataGenerator.getCurrentReading())
    val currentReading: StateFlow<HeartRateReading> =
        _currentReading.asStateFlow()

    private val _todayData =
        MutableStateFlow(HeartRateDataGenerator.getTodayData())
    val todayData: StateFlow<DailyHeartRateData> =
        _todayData.asStateFlow()

    private val _weeklyData =
        MutableStateFlow(HeartRateDataGenerator.getWeeklyData())
    val weeklyData: StateFlow<WeeklyHeartRateData> =
        _weeklyData.asStateFlow()

    private val _zones =
        MutableStateFlow(HeartRateDataGenerator.getHeartRateZones())
    val zones: StateFlow<HeartRateZones> =
        _zones.asStateFlow()

    private val _historicalReadings =
        MutableStateFlow(HeartRateDataGenerator.getHistoricalReadings())
    val historicalReadings: StateFlow<List<HistoricalReading>> =
        _historicalReadings.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: HeartRateRepository? = null

        fun getInstance(): HeartRateRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HeartRateRepository().also { INSTANCE = it }
            }
        }
    }

    fun refreshData() {
        _currentReading.value = HeartRateDataGenerator.getCurrentReading()
        _todayData.value = HeartRateDataGenerator.getTodayData()
        _weeklyData.value = HeartRateDataGenerator.getWeeklyData()
        _zones.value = HeartRateDataGenerator.getHeartRateZones()
        _historicalReadings.value = HeartRateDataGenerator.getHistoricalReadings()
    }
}
