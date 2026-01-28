package com.example.timelycare.ui.screens.heartrate

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { HeartRateRepository.getInstance() }

    val todayData by repository.todayData.collectAsStateWithLifecycle()
    val weeklyData by repository.weeklyData.collectAsStateWithLifecycle()
    val zones by repository.zones.collectAsStateWithLifecycle()
    val historicalReadings by repository.historicalReadings.collectAsStateWithLifecycle()

    val dateNavItems = remember(todayData.date, historicalReadings) {
        val items = historicalReadings.map {
            DateNavItem(it.date, it.displayDate)
        }.toMutableList()

        if (items.none { it.date == todayData.date }) {
            items.add(0, DateNavItem(todayData.date, "Today"))
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
                generateSyntheticDailyHeartRateData(todayData, selectedDate)
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

        item { CurrentReadingCard(dailyData = selectedDailyData) }

        item { DailyHeartRateChart(dailyData = selectedDailyData) }

        item { HeartRateZonesCard(zones = zones) }

        item { WeeklyTrendChart(weeklyData = weeklyData) }

        item { PastWeekHistory(historicalReadings = historicalReadings) }
    }
}

private data class DateNavItem(
    val date: LocalDate,
    val label: String
)

private fun generateSyntheticDailyHeartRateData(
    template: DailyHeartRateData,
    targetDate: LocalDate
): DailyHeartRateData {
    val random = Random(targetDate.toEpochDay())

    val readings = template.readings.map { reading ->
        val delta = random.nextInt(-6, 7)
        val bpm = (reading.bpm + delta).coerceIn(55, 110)

        val zone = when {
            bpm <= 70 -> HeartRateZone.NORMAL
            bpm <= 85 -> HeartRateZone.ELEVATED
            else -> HeartRateZone.HIGH
        }

        HeartRateReading(
            bpm = bpm,
            timestamp = reading.timestamp,
            zone = zone
        )
    }

    val average = readings.map { it.bpm }.average().toInt()

    return template.copy(
        date = targetDate,
        readings = readings,
        average = average,
        min = readings.minOf { it.bpm },
        max = readings.maxOf { it.bpm },
        current = readings.lastOrNull()?.bpm ?: average
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
                    Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
