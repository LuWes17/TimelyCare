package com.example.timelycare.ui.screens.heartrate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.HeartRateZones
import com.example.timelycare.ui.theme.*

@Composable
fun HeartRateZonesCard(
    zones: HeartRateZones,
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
                text = "Heart Rate Zones",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareTextPrimary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeartRateZoneItem(
                    zoneName = "Resting Zone",
                    percentage = zones.restingPercentage,
                    color = Color(0xFF38A169) // Green
                )

                HeartRateZoneItem(
                    zoneName = "Light Activity",
                    percentage = zones.lightActivityPercentage,
                    color = Color(0xFFD69E2E) // Yellow
                )

                HeartRateZoneItem(
                    zoneName = "Moderate Activity",
                    percentage = zones.moderateActivityPercentage,
                    color = Color(0xFFED8936) // Orange
                )

                HeartRateZoneItem(
                    zoneName = "Intense Activity",
                    percentage = zones.intenseActivityPercentage,
                    color = Color(0xFFE53E3E) // Red
                )
            }
        }
    }
}

@Composable
private fun HeartRateZoneItem(
    zoneName: String,
    percentage: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = zoneName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TimelyCareTextPrimary
            )
            Text(
                text = "$percentage%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareTextPrimary
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF1F1F1))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}