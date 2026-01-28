package com.example.timelycare.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.Medication
import com.example.timelycare.data.Frequency
import com.example.timelycare.data.DayOfWeek
import com.example.timelycare.data.MedicationTakenRepository
import com.example.timelycare.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MedicationListForDate(
    selectedDate: LocalDate,
    medications: List<Medication>,
    modifier: Modifier = Modifier
) {
    val now = LocalTime.now()
    val today = LocalDate.now()

    val medicationTimesForDate = try {
        if (medications == null) emptyList()
        else {
            val scheduledMedications = medications.filterNotNull().filter { medication ->
                isMedicationScheduledForDate(medication, selectedDate)
            }

            scheduledMedications.flatMap { medication ->
                medication.medicationTimes.map { time ->
                    Pair(medication, time)
                }
            }.sortedBy { it.second } // SORT BY TIME
        }
    } catch (e: Exception) {
        emptyList()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Medications for ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TimelyCareTextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (medicationTimesForDate.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No medications scheduled for today",
                    fontSize = 16.sp,
                    color = TimelyCareTextSecondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(medicationTimesForDate) { (medication, scheduledTime) ->
                    CalendarMedicationCard(
                        medication = medication,
                        scheduledTime = scheduledTime,
                        date = selectedDate
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarMedicationCard(
    medication: Medication,
    scheduledTime: LocalTime,
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val takenRepository = remember { MedicationTakenRepository.getInstance(context) }
    val takenRecords by takenRepository.takenRecords.collectAsStateWithLifecycle()

    val now = LocalTime.now()
    val today = LocalDate.now()

    val isTaken by remember(takenRecords, date, medication.id, scheduledTime) {
        derivedStateOf {
            takenRepository.isMedicationTaken(medication.id, scheduledTime, date)
        }
    }

    // Determine status text
    val statusText = when {
        isTaken -> "Taken"
        date.isAfter(today) -> "Upcoming" // Future dates
        date.isBefore(today) -> "Missed"  // Past dates
        date == today && now.isBefore(scheduledTime) -> "Upcoming" // Today but time not passed
        date == today && now.isAfter(scheduledTime) -> "Missed"    // Today but time passed
        else -> ""
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (isTaken) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4CAF50)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Pill icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = TimelyCareBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(6.dp)
                                .background(
                                    color = TimelyCareBlue,
                                    shape = RoundedCornerShape(3.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(6.dp)
                                .background(
                                    color = TimelyCareBlue.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .offset(x = (-4).dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = medication.name.takeIf { it.isNotBlank() } ?: "Unknown Medicine",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareTextPrimary
                    )
                    Text(
                        text = medication.dosage.takeIf { it.isNotBlank() } ?: "No dosage",
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = scheduledTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            if (statusText.isNotEmpty()) {
                Text(
                    text = statusText,
                    fontSize = 14.sp,
                    color = when (statusText) {
                        "Taken" -> Color(0xFF4CAF50)
                        "Upcoming" -> TimelyCareTextSecondary
                        "Missed" -> Color(0xFFF44336)
                        else -> TimelyCareTextSecondary
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun isMedicationScheduledForDate(medication: Medication, date: LocalDate): Boolean {
    return try {
        // Defensive null checks
        if (medication == null) return false
        if (date == null) return false

        // Check if medication is within date range
        val startDate = medication.startDate
        val endDate = medication.endDate

        // With required dates, these should never be null, but check defensively
        if (startDate != null && date.isBefore(startDate)) return false
        if (endDate != null && date.isAfter(endDate)) return false

        // Check frequency with defensive null check
        val frequency = medication.frequency ?: return false
        return when (frequency) {
            is Frequency.Daily -> true
            is Frequency.SpecificDays -> {
                try {
                    val dayOfWeek = when (date.dayOfWeek) {
                        java.time.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
                        java.time.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
                        java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
                        java.time.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
                        java.time.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
                        java.time.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
                        java.time.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
                    }
                    frequency.days?.contains(dayOfWeek) ?: false
                } catch (e: Exception) {
                    false
                }
            }
        }
    } catch (e: Exception) {
        // Return false on any error to prevent crash
        false
    }
}