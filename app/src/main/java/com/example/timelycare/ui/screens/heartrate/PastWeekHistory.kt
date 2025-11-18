package com.example.timelycare.ui.screens.heartrate

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Past week",
                    tint = TimelyCareBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Past Week",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                historicalReadings.forEach { reading ->
                    HeartRateHistoryItem(reading = reading)
                }
            }
        }
    }
}

@Composable
private fun HeartRateHistoryItem(
    reading: HistoricalReading,
    modifier: Modifier = Modifier
) {
    val isToday = reading.displayDate == "Today"

    val cardModifier = if (isToday) {
        modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = TimelyCareBlue,
                shape = RoundedCornerShape(8.dp)
            )
    } else {
        modifier.fillMaxWidth()
    }

    Surface(
        modifier = cardModifier,
        color = if (isToday) TimelyCareBlue.copy(alpha = 0.05f) else TimelyCareWhite,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = if (isToday) 0.dp else 1.dp
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
                fontSize = 14.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                color = if (isToday) TimelyCareBlue else TimelyCareTextPrimary,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${reading.bpm}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) TimelyCareBlue else TimelyCareTextPrimary
                )

                StatusIndicator(
                    status = "BPM",
                    color = getZoneTextColor(reading.zone)
                )
            }
        }
    }
}

@Composable
private fun StatusIndicator(
    status: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun getZoneTextColor(zone: HeartRateZone): Color {
    return when (zone) {
        HeartRateZone.NORMAL -> Color(0xFF38A169) // Green
        HeartRateZone.ELEVATED -> Color(0xFFD69E2E) // Yellow/Orange
        HeartRateZone.HIGH -> Color(0xFFE53E3E) // Red
    }
}