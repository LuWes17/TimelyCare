package com.example.timelycare.ui.screens.glucose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.data.DailyGlucoseData
import com.example.timelycare.data.GlucoseCategory
import com.example.timelycare.ui.theme.*

@Composable
fun DailyGlucoseChart(
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Daily glucose",
                    tint = TimelyCareBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Daily Glucose",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimelyCareTextPrimary
                )
            }

            Text(
                text = "Glucose levels throughout the day",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TimelyCareTextSecondary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                GlucoseBarChart(
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
                    color = getGlucoseCategoryColor(GlucoseCategory.LOW),
                    label = "Low (<70)"
                )
                LegendItem(
                    color = getGlucoseCategoryColor(GlucoseCategory.NORMAL),
                    label = "Normal (70-140)"
                )
                LegendItem(
                    color = getGlucoseCategoryColor(GlucoseCategory.HIGH),
                    label = "High (>140)"
                )
            }
        }
    }
}

@Composable
private fun GlucoseBarChart(
    readings: List<com.example.timelycare.data.GlucoseReading>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // Pre-calculate colors outside Canvas
    val colorMap = remember(readings) {
        readings.associateWith { reading ->
            getGlucoseCategoryColorValue(reading.category)
        }
    }

    Canvas(modifier = modifier) {
        val chartWidth = size.width
        val chartHeight = size.height - with(density) { 60.dp.toPx() }
        val barWidth = chartWidth / (readings.size * 1.5f)
        val spacing = barWidth * 0.5f

        val maxValue = 150f
        val minValue = 60f
        val range = maxValue - minValue

        readings.forEachIndexed { index, reading ->
            val x = index * (barWidth + spacing) + spacing
            val color = colorMap[reading]!!

            // Bar
            val barHeight = ((reading.value - minValue) / range) * chartHeight
            drawRect(
                color = color,
                topLeft = Offset(x, chartHeight - barHeight),
                size = Size(barWidth, barHeight)
            )
        }

        // Time labels and values
        readings.forEachIndexed { index, reading ->
            val x = index * (barWidth + spacing) + spacing + barWidth / 2

            // Time label
            drawContext.canvas.nativeCanvas.drawText(
                reading.timestamp,
                x,
                chartHeight + with(density) { 20.dp.toPx() },
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#718096")
                    textSize = with(density) { 12.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )

            // Value label
            drawContext.canvas.nativeCanvas.drawText(
                reading.displayValue,
                x,
                chartHeight + with(density) { 40.dp.toPx() },
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#2D3748")
                    textSize = with(density) { 14.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
            )
        }
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

private fun getGlucoseCategoryColorValue(category: GlucoseCategory): Color {
    return when (category) {
        GlucoseCategory.LOW -> Color(0xFFFFC107) // Yellow/Amber
        GlucoseCategory.NORMAL -> Color(0xFF4CAF50) // Green
        GlucoseCategory.HIGH -> Color(0xFFE53E3E) // Red
    }
}