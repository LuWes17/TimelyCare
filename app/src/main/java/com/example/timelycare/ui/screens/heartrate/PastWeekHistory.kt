package com.example.timelycare.ui.screens.heartrate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.HeartRateZone
import com.example.timelycare.data.HistoricalReading
import com.example.timelycare.ui.theme.*

@Composable
fun PastWeekHistory(
    historicalReadings: List<HistoricalReading>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Past week",
                    tint = Color(0xFF38A169),
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "Past Week",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                historicalReadings.forEach { reading ->
                    HistoricalReadingItem(reading = reading)
                }
            }
        }
    }
}

@Composable
private fun HistoricalReadingItem(
    reading: HistoricalReading,
    modifier: Modifier = Modifier
) {
    val isToday = reading.displayDate == "Today"

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) {
                TimelyCareBlue.copy(alpha = 0.1f)
            } else {
                Color(0xFFF8F9FA)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isToday) 2.dp else 1.dp
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (isToday) {
            androidx.compose.foundation.BorderStroke(1.dp, TimelyCareBlue.copy(alpha = 0.3f))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reading.displayDate,
                fontSize = 16.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                color = if (isToday) TimelyCareBlue else TimelyCareTextPrimary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${reading.bpm}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )

                Text(
                    text = "BPM",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = getZoneTextColor(reading.zone),
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

private fun getZoneTextColor(zone: HeartRateZone): Color {
    return when (zone) {
        HeartRateZone.NORMAL -> Color(0xFF38A169) // Green
        HeartRateZone.ELEVATED -> Color(0xFFD69E2E) // Yellow/Orange
        HeartRateZone.HIGH -> Color(0xFFE53E3E) // Red
    }
}