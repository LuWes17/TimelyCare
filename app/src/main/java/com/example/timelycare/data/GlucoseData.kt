package com.example.timelycare.data

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

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
    fun getCurrentReading(): GlucoseReading {
        return GlucoseReading(
            value = 95,
            timestamp = "Current",
            category = GlucoseCategory.NORMAL
        )
    }

    fun getTodayData(): DailyGlucoseData {
        val hourlyReadings = listOf(
            GlucoseReading(89, "6AM", getGlucoseCategory(89)),
            GlucoseReading(107, "8AM", getGlucoseCategory(107)),
            GlucoseReading(100, "10AM", getGlucoseCategory(100)),
            GlucoseReading(112, "12PM", getGlucoseCategory(112)),
            GlucoseReading(92, "2PM", getGlucoseCategory(92)),
            GlucoseReading(100, "4PM", getGlucoseCategory(100)),
            GlucoseReading(104, "6PM", getGlucoseCategory(104)),
            GlucoseReading(94, "8PM", getGlucoseCategory(94))
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
        return listOf(
            HistoricalGlucoseReading(
                date = today.minusDays(7),
                displayDate = "Wed, Nov 5",
                reading = GlucoseReading(98, "Wed", getGlucoseCategory(98))
            ),
            HistoricalGlucoseReading(
                date = today.minusDays(6),
                displayDate = "Thu, Nov 6",
                reading = GlucoseReading(101, "Thu", getGlucoseCategory(101))
            ),
            HistoricalGlucoseReading(
                date = today.minusDays(5),
                displayDate = "Fri, Nov 7",
                reading = GlucoseReading(108, "Fri", getGlucoseCategory(108))
            ),
            HistoricalGlucoseReading(
                date = today.minusDays(4),
                displayDate = "Sat, Nov 8",
                reading = GlucoseReading(97, "Sat", getGlucoseCategory(97))
            ),
            HistoricalGlucoseReading(
                date = today.minusDays(3),
                displayDate = "Sun, Nov 9",
                reading = GlucoseReading(104, "Sun", getGlucoseCategory(104))
            ),
            HistoricalGlucoseReading(
                date = today.minusDays(2),
                displayDate = "Mon, Nov 10",
                reading = GlucoseReading(111, "Mon", getGlucoseCategory(111))
            ),
            HistoricalGlucoseReading(
                date = today.minusDays(1),
                displayDate = "Yesterday",
                reading = GlucoseReading(95, "Yesterday", getGlucoseCategory(95))
            ),
            HistoricalGlucoseReading(
                date = today,
                displayDate = "Today",
                reading = GlucoseReading(100, "Today", getGlucoseCategory(100)),
                isToday = true
            )
        )
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
    private val _todayData = MutableStateFlow(GlucoseDataGenerator.getTodayData())
    val todayData: StateFlow<DailyGlucoseData> = _todayData.asStateFlow()

    private val _historicalReadings = MutableStateFlow(GlucoseDataGenerator.getHistoricalReadings())
    val historicalReadings: StateFlow<List<HistoricalGlucoseReading>> = _historicalReadings.asStateFlow()

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