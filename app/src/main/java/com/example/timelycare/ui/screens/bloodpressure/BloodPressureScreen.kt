package com.example.timelycare.ui.screens.bloodpressure

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.*
import com.example.timelycare.ui.theme.*
import java.time.LocalDate
import kotlin.random.Random

@Composable
fun BloodPressureScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { BloodPressureRepository.getInstance() }

    val todayData by repository.todayData.collectAsStateWithLifecycle()
    val riskAssessment by repository.riskAssessment.collectAsStateWithLifecycle()
    val historicalReadings by repository.historicalReadings.collectAsStateWithLifecycle()

    val dateNavItems = remember(todayData.date, historicalReadings) {
        val items = mutableListOf<DateNavItem>()
        items.add(DateNavItem(todayData.date, "Today"))
        historicalReadings.forEach {
            items.add(DateNavItem(it.date, it.displayDate))
        }
        items
    }

    var selectedNavIndex by remember(dateNavItems) { mutableIntStateOf(0) }

    LaunchedEffect(dateNavItems.size) {
        if (dateNavItems.isNotEmpty()) {
            selectedNavIndex = selectedNavIndex.coerceIn(0, dateNavItems.lastIndex)
        }
    }

    val selectedDailyData = remember(todayData, dateNavItems, selectedNavIndex) {
        if (dateNavItems.isEmpty()) {
            todayData
        } else {
            val selectedDate = dateNavItems[selectedNavIndex].date
            if (selectedDate == todayData.date) {
                todayData
            } else {
                generateSyntheticDailyBPData(todayData, selectedDate)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {

        item {
            DateNavigationCard(
                items = dateNavItems,
                selectedIndex = selectedNavIndex,
                onPrevious = {
                    if (selectedNavIndex > 0) selectedNavIndex--
                },
                onNext = {
                    if (selectedNavIndex < dateNavItems.lastIndex) selectedNavIndex++
                }
            )
        }

        item { CurrentBPReadingCard(dailyData = selectedDailyData) }

        item { DailyBPChart(dailyData = selectedDailyData) }

        item { BPRangesCard(dailyData = selectedDailyData) }

        item { RiskAssessmentCard(riskAssessment = riskAssessment) }

        item { PastWeekBPHistory(historicalReadings = historicalReadings) }
    }
}

private data class DateNavItem(
    val date: LocalDate,
    val label: String
)

private fun generateSyntheticDailyBPData(
    template: DailyBPData,
    targetDate: LocalDate
): DailyBPData {
    val random = Random(targetDate.toEpochDay())

    val readings = template.readings.map { reading ->
        val systolic = (reading.systolic + random.nextInt(-6, 7)).coerceIn(105, 145)
        val diastolic = (reading.diastolic + random.nextInt(-4, 5)).coerceIn(65, 95)
        val pulse = (reading.pulse + random.nextInt(-6, 7)).coerceIn(60, 110)

        reading.copy(
            systolic = systolic,
            diastolic = diastolic,
            pulse = pulse
        )
    }

    val avgSystolic = readings.map { it.systolic }.average().toInt()
    val avgDiastolic = readings.map { it.diastolic }.average().toInt()
    val avgPulse = readings.map { it.pulse }.average().toInt()

    val averageReading = readings.first().copy(
        systolic = avgSystolic,
        diastolic = avgDiastolic,
        pulse = avgPulse,
        timestamp = "Average"
    )

    val lowest = readings.minByOrNull { it.systolic + it.diastolic } ?: readings.first()
    val highest = readings.maxByOrNull { it.systolic + it.diastolic } ?: readings.last()
    val current = readings.lastOrNull() ?: readings.first()

    return template.copy(
        date = targetDate,
        readings = readings,
        current = current,
        average = averageReading,
        lowest = lowest,
        highest = highest
    )
}

@Composable
private fun DateNavigationCard(
    items: List<DateNavItem>,
    selectedIndex: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasItems = items.isNotEmpty()
    val label = if (hasItems) items[selectedIndex].label else "Today"

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious, enabled = hasItems && selectedIndex > 0) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = TimelyCareBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = label,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareTextPrimary
                    )
                }

                IconButton(
                    onClick = onNext,
                    enabled = hasItems && selectedIndex < items.lastIndex
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                items.forEachIndexed { index, _ ->
                    val active = index == selectedIndex
                    Canvas(
                        modifier = Modifier
                            .size(if (active) 8.dp else 6.dp)
                            .padding(horizontal = 2.dp)
                    ) {
                        drawCircle(
                            color = if (active) TimelyCareBlue else Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}
