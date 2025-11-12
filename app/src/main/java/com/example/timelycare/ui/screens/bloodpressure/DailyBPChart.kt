package com.example.timelycare.ui.screens.bloodpressure

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.DailyBPData
import com.example.timelycare.data.BPCategory
import com.example.timelycare.ui.theme.*

@Composable
fun DailyBPChart(
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
            Text(
                text = "Daily Blood Pressure",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareTextPrimary
            )

            Text(
                text = "Systolic and diastolic readings",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TimelyCareTextSecondary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                BPBarChart(
                    readings = dailyData.readings,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(
                    color = getBPCategoryColor(BPCategory.NORMAL),
                    label = "Normal"
                )
                LegendItem(
                    color = getBPCategoryColor(BPCategory.ELEVATED),
                    label = "Elevated"
                )
                LegendItem(
                    color = getBPCategoryColor(BPCategory.HIGH),
                    label = "High"
                )
            }
        }
    }
}

@Composable
private fun BPBarChart(
    readings: List<com.example.timelycare.data.BloodPressureReading>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // Pre-calculate colors outside Canvas
    val colorMap = remember(readings) {
        readings.associateWith { reading ->
            Pair(
                getBPCategoryColorValue(reading.systolicCategory),
                getBPCategoryColorValue(reading.diastolicCategory)
            )
        }
    }

    Canvas(modifier = modifier) {
        val chartWidth = size.width
        val chartHeight = size.height - with(density) { 40.dp.toPx() }
        val barWidth = chartWidth / (readings.size * 2.5f)
        val spacing = barWidth * 0.5f

        val maxSystolic = 140f
        val minValue = 60f
        val range = maxSystolic - minValue

        readings.forEachIndexed { index, reading ->
            val x = index * (barWidth * 2 + spacing) + spacing
            val colors = colorMap[reading]!!

            // Systolic bar (left)
            val systolicHeight = ((reading.systolic - minValue) / range) * chartHeight
            drawRect(
                color = colors.first,
                topLeft = Offset(x, chartHeight - systolicHeight),
                size = Size(barWidth, systolicHeight)
            )

            // Diastolic bar (right)
            val diastolicHeight = ((reading.diastolic - minValue) / range) * chartHeight
            drawRect(
                color = colors.second,
                topLeft = Offset(x + barWidth + with(density) { 4.dp.toPx() }, chartHeight - diastolicHeight),
                size = Size(barWidth, diastolicHeight)
            )
        }

        // Time labels
        readings.forEachIndexed { index, reading ->
            val x = index * (barWidth * 2 + spacing) + spacing + barWidth
            drawContext.canvas.nativeCanvas.drawText(
                reading.timestamp,
                x,
                chartHeight + with(density) { 30.dp.toPx() },
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#718096")
                    textSize = with(density) { 12.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

private fun getBPCategoryColorValue(category: BPCategory): Color {
    return when (category) {
        BPCategory.NORMAL -> Color(0xFF38A169) // Green
        BPCategory.ELEVATED -> Color(0xFFD69E2E) // Yellow/Orange
        BPCategory.HIGH -> Color(0xFFE53E3E) // Red
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TimelyCareTextSecondary
        )
    }
}