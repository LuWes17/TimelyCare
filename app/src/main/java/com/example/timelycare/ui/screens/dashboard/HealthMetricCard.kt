package com.example.timelycare.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.HealthMetric
import com.example.timelycare.data.HealthMetricIcon
import com.example.timelycare.ui.theme.*

@Composable
fun HealthMetricCard(
    healthMetric: HealthMetric,
    onMetricClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMetricClick(healthMetric.id) },
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = getIconBackgroundColor(healthMetric.iconType),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getHealthMetricIcon(healthMetric.iconType),
                        contentDescription = healthMetric.title,
                        tint = getIconColor(healthMetric.iconType),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = healthMetric.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TimelyCareTextPrimary
                )
            }

            // Value
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = healthMetric.value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = getValueColor(healthMetric.iconType)
                )
                if (healthMetric.unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = healthMetric.unit,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getValueColor(healthMetric.iconType),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun getHealthMetricIcon(iconType: HealthMetricIcon): ImageVector {
    return when (iconType) {
        HealthMetricIcon.HEART_RATE -> Icons.Default.FavoriteBorder
        HealthMetricIcon.BLOOD_PRESSURE -> Icons.Default.Favorite
        HealthMetricIcon.GLUCOSE -> Icons.Default.FavoriteBorder // Using same icon, will be styled differently
    }
}

@Composable
private fun getIconColor(iconType: HealthMetricIcon): Color {
    return when (iconType) {
        // Use theme accent color for all metric icons so they follow theme color
        HealthMetricIcon.HEART_RATE -> TimelyCareBlue
        HealthMetricIcon.BLOOD_PRESSURE -> TimelyCareBlue
        HealthMetricIcon.GLUCOSE -> TimelyCareBlue
    }
}

@Composable
private fun getIconBackgroundColor(iconType: HealthMetricIcon): Color {
    return when (iconType) {
        // Use a faint accent background for all metric icons
        HealthMetricIcon.HEART_RATE -> TimelyCareBlue.copy(alpha = 0.1f)
        HealthMetricIcon.BLOOD_PRESSURE -> TimelyCareBlue.copy(alpha = 0.1f)
        HealthMetricIcon.GLUCOSE -> TimelyCareBlue.copy(alpha = 0.1f)
    }
}

@Composable
private fun getValueColor(iconType: HealthMetricIcon): Color {
    return when (iconType) {
        HealthMetricIcon.HEART_RATE -> Color(0xFF38A169) // Green for good value
        HealthMetricIcon.BLOOD_PRESSURE -> Color(0xFF38A169) // Green for good value
        HealthMetricIcon.GLUCOSE -> Color(0xFF38A169) // Green for good value
    }
}