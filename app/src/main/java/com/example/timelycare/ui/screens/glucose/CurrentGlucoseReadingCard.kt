package com.example.timelycare.ui.screens.glucose

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
import com.example.timelycare.data.DailyGlucoseData
import com.example.timelycare.data.GlucoseCategory
import com.example.timelycare.ui.theme.*

@Composable
fun CurrentGlucoseReadingCard(
    dailyData: DailyGlucoseData,
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
                    imageVector = Icons.Default.Star,
                    contentDescription = "Glucose",
                    tint = getGlucoseCategoryColor(dailyData.current.category),
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "Glucose Reading",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dailyData.current.category.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = getGlucoseCategoryColor(dailyData.current.category)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Large glucose display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dailyData.current.displayValue,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
                Text(
                    text = "mg/dL",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TimelyCareTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Statistics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GlucoseStatisticItem(
                    value = dailyData.average.displayValue,
                    label = "Average"
                )
                GlucoseStatisticItem(
                    value = dailyData.min.displayValue,
                    label = "Min"
                )
                GlucoseStatisticItem(
                    value = dailyData.max.displayValue,
                    label = "Max"
                )
            }
        }
    }
}

@Composable
private fun GlucoseStatisticItem(
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
fun getGlucoseCategoryColor(category: GlucoseCategory): Color {
    return when (category) {
        GlucoseCategory.LOW -> Color(0xFFFFC107) // Yellow/Amber
        GlucoseCategory.NORMAL -> Color(0xFF4CAF50) // Green
        GlucoseCategory.HIGH -> Color(0xFFE53E3E) // Red
    }
}