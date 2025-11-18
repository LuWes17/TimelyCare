package com.example.timelycare.ui.screens.heartrate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.DailyHeartRateData
import com.example.timelycare.data.HeartRateZone
import com.example.timelycare.data.HeartRateReading
import com.example.timelycare.ui.theme.*

@Composable
fun DailyHeartRateChart(
    dailyData: DailyHeartRateData,
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
                    imageVector = Icons.Default.Info,
                    contentDescription = "Daily heart rate",
                    tint = TimelyCareBlue,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = "Daily Heart Rate",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareTextPrimary
                    )
                    Text(
                        text = "Your heart rate throughout the day",
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary
                    )
                }
            }

            // Chart
            HeartRateBarChart(
                readings = dailyData.readings,
                modifier = Modifier.fillMaxWidth()
            )

            // Legend
            HeartRateZoneLegend()
        }
    }
}

@Composable
private fun HeartRateBarChart(
    readings: List<HeartRateReading>,
    modifier: Modifier = Modifier
) {
    val maxBpm = readings.maxOfOrNull { it.bpm } ?: 100
    val minBpm = readings.minOfOrNull { it.bpm } ?: 60
    val range = maxBpm - minBpm

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Bar chart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            readings.forEach { reading ->
                val heightRatio = if (range > 0) {
                    (reading.bpm - minBpm).toFloat() / range.toFloat()
                } else {
                    0.5f
                }
                val barHeight = 100.dp + (100.dp * heightRatio)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Bar
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(getZoneColor(reading.zone))
                    )
                }
            }
        }

        // Time labels and values
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            readings.forEach { reading ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = reading.timestamp,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TimelyCareTextSecondary
                    )
                    Text(
                        text = "${reading.bpm}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun HeartRateZoneLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(
            color = Color(0xFF38A169), // Green
            label = "Normal"
        )
        LegendItem(
            color = Color(0xFFD69E2E), // Yellow
            label = "Elevated"
        )
        LegendItem(
            color = Color(0xFFE53E3E), // Red
            label = "High"
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TimelyCareTextSecondary
        )
    }
}

private fun getZoneColor(zone: HeartRateZone): Color {
    return when (zone) {
        HeartRateZone.NORMAL -> Color(0xFF38A169) // Green
        HeartRateZone.ELEVATED -> Color(0xFFD69E2E) // Yellow
        HeartRateZone.HIGH -> Color(0xFFE53E3E) // Red
    }
}