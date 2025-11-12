package com.example.timelycare.ui.screens.bloodpressure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.HistoricalBPReading
import com.example.timelycare.ui.theme.*

@Composable
fun PastWeekBPHistory(
    historicalReadings: List<HistoricalBPReading>,
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
            Text(
                text = "Past Week",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareTextPrimary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                historicalReadings.forEach { reading ->
                    BPHistoryItem(reading = reading)
                }
            }
        }
    }
}

@Composable
private fun BPHistoryItem(
    reading: HistoricalBPReading,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = reading.displayDate,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TimelyCareTextPrimary,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = reading.reading.reading,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareTextPrimary
            )

            StatusIndicator(
                status = reading.reading.overallCategory.displayName,
                color = getBPCategoryColor(reading.reading.overallCategory)
            )
        }
    }
}

@Composable
private fun StatusIndicator(
    status: String,
    color: androidx.compose.ui.graphics.Color,
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