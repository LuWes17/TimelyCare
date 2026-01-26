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
        get() =
            if (systolicCategory.ordinal > diastolicCategory.ordinal)
                systolicCategory
            else
                diastolicCategory

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

    private val DAY_FORMATTER =
        DateTimeFormatter.ofPattern("EEE, MMM d")

    fun getCurrentReading(): BloodPressureReading {
        return BloodPressureReading(
            systolic = 120,
            diastolic = 80,
            pulse = 79,
            timestamp = "Now",
            category = BPCategory.ELEVATED
        )
    }

    fun getTodayData(): DailyBPData {
        val hourlyReadings = listOf(
            BloodPressureReading(112, 71, 75, "6 AM", BPCategory.NORMAL),
            BloodPressureReading(119, 77, 78, "8 AM", BPCategory.NORMAL),
            BloodPressureReading(121, 78, 80, "10 AM", BPCategory.ELEVATED),
            BloodPressureReading(123, 79, 82, "12 PM", BPCategory.ELEVATED),
            BloodPressureReading(120, 80, 79, "2 PM", BPCategory.ELEVATED),
            BloodPressureReading(118, 78, 77, "4 PM", BPCategory.NORMAL),
            BloodPressureReading(115, 75, 74, "6 PM", BPCategory.NORMAL),
            BloodPressureReading(113, 73, 72, "8 PM", BPCategory.NORMAL)
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

        val sampleValues = listOf(
            Triple(120, 80, 79),
            Triple(121, 82, 83),
            Triple(119, 77, 78),
            Triple(125, 79, 81),
            Triple(120, 80, 79),
            Triple(115, 75, 74),
            Triple(122, 81, 82),
            Triple(118, 78, 76)
        )

        fun displayLabel(date: LocalDate): String =
            when (date) {
                today -> "Today"
                today.minusDays(1) -> "Yesterday"
                else -> date.format(DAY_FORMATTER)
            }

        return (0..7).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val (sys, dia, pulse) = sampleValues[daysAgo]

            HistoricalBPReading(
                date = date,
                displayDate = displayLabel(date),
                reading = BloodPressureReading(
                    systolic = sys,
                    diastolic = dia,
                    pulse = pulse,
                    timestamp = displayLabel(date),
                    category = when {
                        sys < 120 && dia < 80 -> BPCategory.NORMAL
                        sys in 120..129 || dia in 80..89 -> BPCategory.ELEVATED
                        else -> BPCategory.HIGH
                    }
                )
            )
        }
    }
}

class BloodPressureRepository private constructor() {

    private val _todayData =
        MutableStateFlow(BloodPressureDataGenerator.getTodayData())
    val todayData: StateFlow<DailyBPData> =
        _todayData.asStateFlow()

    private val _riskAssessment =
        MutableStateFlow(BloodPressureDataGenerator.getRiskAssessment())
    val riskAssessment: StateFlow<BPRiskAssessment> =
        _riskAssessment.asStateFlow()

    private val _historicalReadings =
        MutableStateFlow(BloodPressureDataGenerator.getHistoricalReadings())
    val historicalReadings: StateFlow<List<HistoricalBPReading>> =
        _historicalReadings.asStateFlow()

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
