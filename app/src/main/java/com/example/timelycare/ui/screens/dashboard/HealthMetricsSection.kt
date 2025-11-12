package com.example.timelycare.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.HealthMetric
import com.example.timelycare.data.HealthMetricsRepository
import com.example.timelycare.ui.theme.TimelyCareTextPrimary

@Composable
fun HealthMetricsSection(
    onMetricClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { HealthMetricsRepository.getInstance() }
    val healthMetrics by repository.healthMetrics.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Health Metrics",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TimelyCareTextPrimary
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            healthMetrics.forEach { metric ->
                HealthMetricCard(
                    healthMetric = metric,
                    onMetricClick = onMetricClick
                )
            }
        }
    }
}