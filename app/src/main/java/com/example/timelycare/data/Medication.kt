package com.example.timelycare.data

import androidx.compose.runtime.Stable
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Stable
data class Medication(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dosage: String,
    val type: MedicationType,
    val frequency: Frequency,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val medicationTimes: List<LocalTime>,
    val specialInstructions: String = "",
    val isMaintenanceMed: Boolean = false
)

enum class MedicationType {
    PILL, TABLET, CAPSULE, LIQUID, INJECTION, OTHERS;

    override fun toString(): String = when (this) {
        PILL -> "Pill"
        TABLET -> "Tablet"
        CAPSULE -> "Capsule"
        LIQUID -> "Liquid"
        INJECTION -> "Injection"
        OTHERS -> "Others"
    }

    companion object {
        fun fromString(value: String): MedicationType = when (value) {
            "Pill" -> PILL
            "Tablet" -> TABLET
            "Capsule" -> CAPSULE
            "Liquid" -> LIQUID
            "Injection" -> INJECTION
            "Others" -> OTHERS
            else -> PILL
        }
    }
}

sealed class Frequency {
    object Daily : Frequency()
    data class SpecificDays(val days: Set<DayOfWeek>) : Frequency()

    override fun toString(): String = when (this) {
        is Daily -> "Daily"
        is SpecificDays -> if (days.size == 7) "Daily" else days.joinToString(", ")
    }
}

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    override fun toString(): String = when (this) {
        MONDAY -> "Mon"
        TUESDAY -> "Tue"
        WEDNESDAY -> "Wed"
        THURSDAY -> "Thu"
        FRIDAY -> "Fri"
        SATURDAY -> "Sat"
        SUNDAY -> "Sun"
    }
}