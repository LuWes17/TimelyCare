package com.example.timelycare.ui.screens.heartrate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.DailyHeartRateData
import com.example.timelycare.ui.theme.*

@Composable
fun CurrentReadingCard(
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Heart rate",
                    tint = Color(0xFFE53E3E),
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "Current Reading",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Large BPM display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${dailyData.current}",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
                Text(
                    text = "BPM",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TimelyCareTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Statistics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    value = "${dailyData.average}",
                    label = "Average"
                )
                StatisticItem(
                    value = "${dailyData.min}",
                    label = "Min"
                )
                StatisticItem(
                    value = "${dailyData.max}",
                    label = "Max"
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TimelyCareTextPrimary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TimelyCareTextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}