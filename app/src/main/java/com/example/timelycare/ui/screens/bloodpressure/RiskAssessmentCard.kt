package com.example.timelycare.ui.screens.bloodpressure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.BPRiskAssessment
import com.example.timelycare.data.RiskLevel
import com.example.timelycare.ui.theme.*

@Composable
fun RiskAssessmentCard(
    riskAssessment: BPRiskAssessment,
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Risk assessment",
                    tint = getRiskLevelColor(riskAssessment.riskLevel),
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "Risk Assessment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = riskAssessment.riskLevel.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = getRiskLevelColor(riskAssessment.riskLevel)
                )

                RiskIndicator(riskLevel = riskAssessment.riskLevel)
            }

            Text(
                text = riskAssessment.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TimelyCareTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun RiskIndicator(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        repeat(3) { index ->
            val isActive = when (riskLevel) {
                RiskLevel.LOW -> index < 1
                RiskLevel.MODERATE -> index < 2
                RiskLevel.HIGH -> index < 3
            }
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(6.dp)
                    .background(
                        color = if (isActive) getRiskLevelColor(riskLevel) else TimelyCareGray200,
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}

@Composable
fun getRiskLevelColor(riskLevel: RiskLevel): androidx.compose.ui.graphics.Color {
    return when (riskLevel) {
        RiskLevel.LOW -> androidx.compose.ui.graphics.Color(0xFF38A169) // Green
        RiskLevel.MODERATE -> androidx.compose.ui.graphics.Color(0xFFD69E2E) // Yellow/Orange
        RiskLevel.HIGH -> androidx.compose.ui.graphics.Color(0xFFE53E3E) // Red
    }
}