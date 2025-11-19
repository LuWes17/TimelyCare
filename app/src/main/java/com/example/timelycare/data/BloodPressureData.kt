package com.example.timelycare.data

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Stable
data class BloodPressureReading(
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int = 0,
    val timestamp: String,
    val category: BPCategory
) {
    val reading: String
        get() = "$systolic/$diastolic"

    val map: Int
        get() = (2 * diastolic + systolic) / 3

    val systolicCategory: BPCategory
        get() = when {
            systolic < 120 -> BPCategory.NORMAL
            systolic in 120..129 -> BPCategory.ELEVATED
            else -> BPCategory.HIGH
        }

    val diastolicCategory: BPCategory
        get() = when {
            diastolic < 80 -> BPCategory.NORMAL
            diastolic in 80..89 -> BPCategory.ELEVATED
            else -> BPCategory.HIGH
        }

    val overallCategory: BPCategory
        get() = if (systolicCategory.ordinal > diastolicCategory.ordinal) systolicCategory else diastolicCategory

    val statusDisplay: String
        get() = "${systolicCategory.displayName} • ${diastolicCategory.displayName}"
}

@Stable
data class DailyBPData(
    val date: LocalDate,
    val readings: List<BloodPressureReading>,
    val current: BloodPressureReading,
    val average: BloodPressureReading,
    val lowest: BloodPressureReading,
    val highest: BloodPressureReading
)

@Stable
data class BPRiskAssessment(
    val riskLevel: RiskLevel,
    val description: String
)

@Stable
data class HistoricalBPReading(
    val date: LocalDate,
    val displayDate: String,
    val reading: BloodPressureReading
)

enum class BPCategory(val displayName: String) {
    NORMAL("Normal"),
    ELEVATED("Elevated"),
    HIGH("High")
}

enum class RiskLevel(val displayName: String) {
    LOW("Low Risk"),
    MODERATE("Moderate Risk"),
    HIGH("High Risk")
}

object BloodPressureDataGenerator {
    fun getCurrentReading(): BloodPressureReading {
        return BloodPressureReading(
            systolic = 120,
            diastolic = 80,
            pulse = 79,
            timestamp = "Current",
            category = BPCategory.ELEVATED
        )
    }

    fun getTodayData(): DailyBPData {
        val hourlyReadings = listOf(
            BloodPressureReading(112, 71, 75, "6AM", BPCategory.NORMAL),
            BloodPressureReading(119, 77, 78, "8AM", BPCategory.NORMAL),
            BloodPressureReading(121, 78, 80, "10AM", BPCategory.ELEVATED),
            BloodPressureReading(123, 79, 82, "12PM", BPCategory.ELEVATED),
            BloodPressureReading(120, 80, 79, "2PM", BPCategory.ELEVATED),
            BloodPressureReading(118, 78, 77, "4PM", BPCategory.NORMAL),
            BloodPressureReading(115, 75, 74, "6PM", BPCategory.NORMAL),
            BloodPressureReading(113, 73, 72, "8PM", BPCategory.NORMAL)
        )

        val current = getCurrentReading()
        val average = BloodPressureReading(117, 76, 77, "Average", BPCategory.NORMAL)
        val lowest = BloodPressureReading(109, 70, 70, "Lowest", BPCategory.NORMAL)
        val highest = BloodPressureReading(132, 83, 85, "Highest", BPCategory.HIGH)

        return DailyBPData(
            date = LocalDate.now(),
            readings = hourlyReadings,
            current = current,
            average = average,
            lowest = lowest,
            highest = highest
        )
    }

    fun getRiskAssessment(): BPRiskAssessment {
        return BPRiskAssessment(
            riskLevel = RiskLevel.LOW,
            description = "Your blood pressure is within normal range. Continue healthy habits."
        )
    }

    fun getHistoricalReadings(): List<HistoricalBPReading> {
        val today = LocalDate.now()
        return listOf(
            HistoricalBPReading(
                date = today,
                displayDate = "Today",
                reading = BloodPressureReading(120, 80, 79, "Today", BPCategory.ELEVATED)
            ),
            HistoricalBPReading(
                date = today.minusDays(1),
                displayDate = "Yesterday",
                reading = BloodPressureReading(121, 82, 83, "Yesterday", BPCategory.ELEVATED)
            ),
            HistoricalBPReading(
                date = today.minusDays(2),
                displayDate = "Mon, Nov 10",
                reading = BloodPressureReading(119, 77, 78, "Mon", BPCategory.NORMAL)
            ),
            HistoricalBPReading(
                date = today.minusDays(3),
                displayDate = "Sun, Nov 9",
                reading = BloodPressureReading(125, 79, 81, "Sun", BPCategory.ELEVATED)
            ),
            HistoricalBPReading(
                date = today.minusDays(4),
                displayDate = "Sat, Nov 8",
                reading = BloodPressureReading(120, 80, 79, "Sat", BPCategory.ELEVATED)
            ),
            HistoricalBPReading(
                date = today.minusDays(5),
                displayDate = "Fri, Nov 7",
                reading = BloodPressureReading(115, 75, 74, "Fri", BPCategory.NORMAL)
            ),
            HistoricalBPReading(
                date = today.minusDays(6),
                displayDate = "Thu, Nov 6",
                reading = BloodPressureReading(122, 81, 82, "Thu", BPCategory.ELEVATED)
            ),
            HistoricalBPReading(
                date = today.minusDays(7),
                displayDate = "Wed, Nov 5",
                reading = BloodPressureReading(118, 78, 76, "Wed", BPCategory.NORMAL)
            )
        )
    }
}

class BloodPressureRepository private constructor() {
    private val _todayData = MutableStateFlow(BloodPressureDataGenerator.getTodayData())
    val todayData: StateFlow<DailyBPData> = _todayData.asStateFlow()

    private val _riskAssessment = MutableStateFlow(BloodPressureDataGenerator.getRiskAssessment())
    val riskAssessment: StateFlow<BPRiskAssessment> = _riskAssessment.asStateFlow()

    private val _historicalReadings = MutableStateFlow(BloodPressureDataGenerator.getHistoricalReadings())
    val historicalReadings: StateFlow<List<HistoricalBPReading>> = _historicalReadings.asStateFlow()

    companion object {
        @Volatile
        private var INSTANCE: BloodPressureRepository? = null

        fun getInstance(): BloodPressureRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BloodPressureRepository().also { INSTANCE = it }
            }
        }
    }

    fun refreshData() {
        _todayData.value = BloodPressureDataGenerator.getTodayData()
        _riskAssessment.value = BloodPressureDataGenerator.getRiskAssessment()
        _historicalReadings.value = BloodPressureDataGenerator.getHistoricalReadings()
    }
}