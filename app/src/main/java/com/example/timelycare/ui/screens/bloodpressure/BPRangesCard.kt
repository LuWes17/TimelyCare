package com.example.timelycare.ui.screens.bloodpressure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.DailyBPData
import com.example.timelycare.ui.theme.*

@Composable
fun BPRangesCard(
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Blood Pressure Ranges",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BPRangeItem(
                    reading = dailyData.lowest.reading,
                    label = "Lowest Today",
                    category = dailyData.lowest.overallCategory
                )

                Divider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp),
                    color = TimelyCareGray200
                )

                BPRangeItem(
                    reading = dailyData.highest.reading,
                    label = "Highest Today",
                    category = dailyData.highest.overallCategory
                )
            }
        }
    }
}

@Composable
private fun BPRangeItem(
    reading: String,
    label: String,
    category: com.example.timelycare.data.BPCategory,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = reading,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = getBPCategoryColor(category)
        )
        Text(
            text = "mmHg",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TimelyCareTextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TimelyCareTextSecondary
        )
    }
}