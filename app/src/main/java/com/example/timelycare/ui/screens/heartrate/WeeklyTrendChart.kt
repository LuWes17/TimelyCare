package com.example.timelycare.ui.screens.heartrate

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.WeeklyHeartRateData
import com.example.timelycare.ui.theme.*

@Composable
fun WeeklyTrendChart(
    weeklyData: WeeklyHeartRateData,
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
                    imageVector = Icons.Default.Star,
                    contentDescription = "Weekly trend",
                    tint = Color(0xFF38A169),
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = "Weekly Trend",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareTextPrimary
                    )
                    Text(
                        text = "7-day average heart rate",
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary
                    )
                }
            }

            // Chart
            WeeklyLineChart(
                weeklyData = weeklyData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // Day labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklyData.weekDays.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = day.dayName.take(3), // Abbreviate day names
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TimelyCareTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyLineChart(
    weeklyData: WeeklyHeartRateData,
    modifier: Modifier = Modifier
) {
    val readings = weeklyData.weekDays.map { it.averageBpm }
    val maxBpm = readings.maxOrNull() ?: 80
    val minBpm = readings.minOrNull() ?: 60
    val range = maxBpm - minBpm

    Canvas(
        modifier = modifier
    ) {
        if (readings.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val padding = 40.dp.toPx()

        // Available drawing area
        val chartWidth = width - (2 * padding)
        val chartHeight = height - (2 * padding)

        // Draw grid lines
        drawGridLines(
            width = chartWidth,
            height = chartHeight,
            offsetX = padding,
            offsetY = padding
        )

        // Calculate points
        val points = readings.mapIndexed { index, bpm ->
            val x = padding + (index * chartWidth / (readings.size - 1))
            val normalizedValue = if (range > 0) (bpm - minBpm).toFloat() / range else 0.5f
            val y = padding + chartHeight - (normalizedValue * chartHeight)
            Offset(x, y)
        }

        // Draw line connecting points
        if (points.size > 1) {
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = Color(0xFF4299E1), // Blue
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw points
        points.forEach { point ->
            drawCircle(
                color = Color(0xFF4299E1),
                radius = 6.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = point
            )
        }
    }
}

private fun DrawScope.drawGridLines(
    width: Float,
    height: Float,
    offsetX: Float,
    offsetY: Float
) {
    val gridColor = Color(0xFFF1F1F1)
    val strokeWidth = 1.dp.toPx()

    // Horizontal grid lines
    for (i in 0..4) {
        val y = offsetY + (i * height / 4)
        drawLine(
            color = gridColor,
            start = Offset(offsetX, y),
            end = Offset(offsetX + width, y),
            strokeWidth = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
        )
    }

    // Vertical grid lines
    for (i in 0..6) {
        val x = offsetX + (i * width / 6)
        drawLine(
            color = gridColor,
            start = Offset(x, offsetY),
            end = Offset(x, offsetY + height),
            strokeWidth = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
        )
    }
}