package com.example.timelycare.ui.screens.bloodpressure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.DailyBPData
import com.example.timelycare.data.BPCategory
import com.example.timelycare.ui.theme.*

@Composable
fun CurrentBPReadingCard(
    dailyData: DailyBPData,
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
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Blood pressure",
                    tint = TimelyCareBlue,
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

            // Large BP display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dailyData.current.reading,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
                Text(
                    text = "mmHg",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TimelyCareTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Statistics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BPStatisticItem(
                    value = dailyData.average.reading,
                    label = "Average"
                )
                BPStatisticItem(
                    value = "${dailyData.current.pulse}",
                    label = "Pulse"
                )
                BPStatisticItem(
                    value = "${dailyData.current.map}",
                    label = "MAP"
                )
            }
        }
    }
}

@Composable
private fun BPStatisticItem(
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
            fontSize = 20.sp,
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

@Composable
fun getBPCategoryColor(category: BPCategory): Color {
    return when (category) {
        BPCategory.NORMAL -> Color(0xFF38A169) // Green
        BPCategory.ELEVATED -> Color(0xFFD69E2E) // Yellow/Orange
        BPCategory.HIGH -> Color(0xFFE53E3E) // Red
    }
}