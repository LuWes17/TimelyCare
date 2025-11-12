package com.example.timelycare.data

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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
    ELEVATED,  // 71-80
    HIGH       // ≥81
}

enum class HeartRateZoneColor {
    GREEN,
    YELLOW,
    RED
}

object HeartRateDataGenerator {
    fun getCurrentReading(): HeartRateReading {
        return HeartRateReading(
            bpm = 72,
            timestamp = "Current",
            zone = HeartRateZone.ELEVATED
        )
    }

    fun getTodayData(): DailyHeartRateData {
        val hourlyReadings = listOf(
            HeartRateReading(64, "6AM", HeartRateZone.NORMAL),
            HeartRateReading(71, "8AM", HeartRateZone.ELEVATED),
            HeartRateReading(77, "10AM", HeartRateZone.ELEVATED),
            HeartRateReading(74, "12PM", HeartRateZone.ELEVATED),
            HeartRateReading(69, "2PM", HeartRateZone.NORMAL),
            HeartRateReading(66, "4PM", HeartRateZone.NORMAL),
            HeartRateReading(65, "6PM", HeartRateZone.NORMAL),
            HeartRateReading(64, "8PM", HeartRateZone.NORMAL)
        )

        return DailyHeartRateData(
            date = LocalDate.now(),
            readings = hourlyReadings,
            average = 67,
            min = 61,
            max = 84,
            current = 72
        )
    }

    fun getWeeklyData(): WeeklyHeartRateData {
        val today = LocalDate.now()
        val weekDays = (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val dayName = when (daysAgo) {
                0 -> "Today"
                1 -> "Yesterday"
                else -> date.format(DateTimeFormatter.ofPattern("EEE"))
            }
            val bpm = when (daysAgo) {
                0 -> 67 // Today
                1 -> 71 // Yesterday
                2 -> 69 // Mon
                3 -> 74 // Sun
                4 -> 70 // Sat
                5 -> 65 // Fri
                6 -> 72 // Thu
                else -> 68
            }

            WeeklyHeartRateData.DailySummary(
                date = date,
                dayName = dayName,
                averageBpm = bpm
            )
        }

        return WeeklyHeartRateData(weekDays)
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
        return listOf(
            HistoricalReading(
                date = today,
                displayDate = "Today",
                bpm = 67,
                zone = HeartRateZone.NORMAL
            ),
            HistoricalReading(
                date = today.minusDays(1),
                displayDate = "Yesterday",
                bpm = 71,
                zone = HeartRateZone.ELEVATED
            ),
            HistoricalReading(
                date = today.minusDays(2),
                displayDate = "Mon, Nov 10",
                bpm = 69,
                zone = HeartRateZone.NORMAL
            ),
            HistoricalReading(
                date = today.minusDays(3),
                displayDate = "Sun, Nov 9",
                bpm = 74,
                zone = HeartRateZone.ELEVATED
            ),
            HistoricalReading(
                date = today.minusDays(4),
                displayDate = "Sat, Nov 8",
                bpm = 70,
                zone = HeartRateZone.NORMAL
            ),
            HistoricalReading(
                date = today.minusDays(5),
                displayDate = "Fri, Nov 7",
                bpm = 65,
                zone = HeartRateZone.NORMAL
            ),
            HistoricalReading(
                date = today.minusDays(6),
                displayDate = "Thu, Nov 6",
                bpm = 72,
                zone = HeartRateZone.ELEVATED
            ),
            HistoricalReading(
                date = today.minusDays(7),
                displayDate = "Wed, Nov 5",
                bpm = 68,
                zone = HeartRateZone.NORMAL
            )
        )
    }
}

class HeartRateRepository private constructor() {
    private val _currentReading = MutableStateFlow(HeartRateDataGenerator.getCurrentReading())
    val currentReading: StateFlow<HeartRateReading> = _currentReading.asStateFlow()

    private val _todayData = MutableStateFlow(HeartRateDataGenerator.getTodayData())
    val todayData: StateFlow<DailyHeartRateData> = _todayData.asStateFlow()

    private val _weeklyData = MutableStateFlow(HeartRateDataGenerator.getWeeklyData())
    val weeklyData: StateFlow<WeeklyHeartRateData> = _weeklyData.asStateFlow()

    private val _zones = MutableStateFlow(HeartRateDataGenerator.getHeartRateZones())
    val zones: StateFlow<HeartRateZones> = _zones.asStateFlow()

    private val _historicalReadings = MutableStateFlow(HeartRateDataGenerator.getHistoricalReadings())
    val historicalReadings: StateFlow<List<HistoricalReading>> = _historicalReadings.asStateFlow()

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