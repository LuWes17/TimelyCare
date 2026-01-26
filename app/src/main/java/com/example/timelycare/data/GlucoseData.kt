package com.example.timelycare.data

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Stable
data class GlucoseReading(
    val value: Int,
    val timestamp: String,
    val category: GlucoseCategory
) {
    val displayValue: String
        get() = value.toString()
}

@Stable
data class DailyGlucoseData(
    val date: LocalDate,
    val readings: List<GlucoseReading>,
    val current: GlucoseReading,
    val average: GlucoseReading,
    val min: GlucoseReading,
    val max: GlucoseReading
)

@Stable
data class HistoricalGlucoseReading(
    val date: LocalDate,
    val displayDate: String,
    val reading: GlucoseReading,
    val isToday: Boolean = false
)

enum class GlucoseCategory(val displayName: String) {
    LOW("Low"),
    NORMAL("Normal"),
    HIGH("High")
}

object GlucoseDataGenerator {

    private val DAY_FORMATTER =
        DateTimeFormatter.ofPattern("EEE, MMM d")

    fun getCurrentReading(): GlucoseReading {
        return GlucoseReading(
            value = 95,
            timestamp = "Now",
            category = GlucoseCategory.NORMAL
        )
    }

    fun getTodayData(): DailyGlucoseData {
        val hourlyReadings = listOf(
            GlucoseReading(89, "6 AM", getGlucoseCategory(89)),
            GlucoseReading(107, "8 AM", getGlucoseCategory(107)),
            GlucoseReading(100, "10 AM", getGlucoseCategory(100)),
            GlucoseReading(112, "12 PM", getGlucoseCategory(112)),
            GlucoseReading(92, "2 PM", getGlucoseCategory(92)),
            GlucoseReading(100, "4 PM", getGlucoseCategory(100)),
            GlucoseReading(104, "6 PM", getGlucoseCategory(104)),
            GlucoseReading(94, "8 PM", getGlucoseCategory(94))
        )

        val current = getCurrentReading()
        val average = GlucoseReading(100, "Average", getGlucoseCategory(100))
        val min = GlucoseReading(87, "Min", getGlucoseCategory(87))
        val max = GlucoseReading(122, "Max", getGlucoseCategory(122))

        return DailyGlucoseData(
            date = LocalDate.now(),
            readings = hourlyReadings,
            current = current,
            average = average,
            min = min,
            max = max
        )
    }

    fun getHistoricalReadings(): List<HistoricalGlucoseReading> {
        val today = LocalDate.now()

        val sampleValues = listOf(100, 95, 111, 104, 97, 108, 101, 98)

        fun displayLabel(date: LocalDate): String =
            when (date) {
                today -> "Today"
                today.minusDays(1) -> "Yesterday"
                else -> date.format(DAY_FORMATTER)
            }

        return (0..7).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val value = sampleValues[daysAgo]

            HistoricalGlucoseReading(
                date = date,
                displayDate = displayLabel(date),
                reading = GlucoseReading(
                    value = value,
                    timestamp = displayLabel(date),
                    category = getGlucoseCategory(value)
                ),
                isToday = daysAgo == 0
            )
        }
    }

    private fun getGlucoseCategory(value: Int): GlucoseCategory {
        return when {
            value < 70 -> GlucoseCategory.LOW
            value <= 140 -> GlucoseCategory.NORMAL
            else -> GlucoseCategory.HIGH
        }
    }
}

class GlucoseRepository private constructor() {

    private val _todayData =
        MutableStateFlow(GlucoseDataGenerator.getTodayData())
    val todayData: StateFlow<DailyGlucoseData> =
        _todayData.asStateFlow()

    private val _historicalReadings =
        MutableStateFlow(GlucoseDataGenerator.getHistoricalReadings())
    val historicalReadings: StateFlow<List<HistoricalGlucoseReading>> =
        _historicalReadings.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: GlucoseRepository? = null

        fun getInstance(): GlucoseRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GlucoseRepository().also { INSTANCE = it }
            }
        }
    }

    fun refreshData() {
        _todayData.value = GlucoseDataGenerator.getTodayData()
        _historicalReadings.value = GlucoseDataGenerator.getHistoricalReadings()
    }
}
