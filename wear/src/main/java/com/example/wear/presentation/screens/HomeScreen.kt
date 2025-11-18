package com.example.wear.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.example.wear.data.settings.AppSettings
import com.example.wear.data.settings.WatchType
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    onNavigateToScreen: (String) -> Unit,
    settings: AppSettings = AppSettings()
) {
    // We now track the full Date object for analog watch hands
    var currentTime by remember { mutableStateOf(Date()) }
    var heartRate by remember { mutableIntStateOf(68) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTime = now

            // Simulate heart rate variation (65-72 BPM)
            heartRate = (65..72).random()
            delay(1000) // Update every second for smooth analog seconds hand
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        // --- 1. Analog Watch Face Layer (Bottom Layer) ---
        if (settings.watchType == WatchType.ANALOG) {
            AnalogWatchFace(
                currentTime = currentTime,
                modifier = Modifier.fillMaxSize().padding(12.dp),
                primaryColor = MaterialTheme.colors.primary,
                onBackground = MaterialTheme.colors.onBackground,
                onSurface = MaterialTheme.colors.onSurfaceVariant
            )
        }

        // --- 2. Central Content Layer (Digital time, Date, Heart Rate) ---
        // This layer is visible in both modes, but its content changes.
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

            if (settings.watchType == WatchType.DIGITAL) {
                // Digital Time
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                Text(
                    text = timeFormat.format(currentTime),
                    style = MaterialTheme.typography.display1,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground
                )
            } else {
                // Analog Placeholder (to keep elements centered correctly)
                // This creates vertical space so the other elements aren't right in the center
                Spacer(modifier = Modifier.height(30.dp))
            }

            // Date Display
            Text(
                text = dateFormat.format(currentTime),
                style = MaterialTheme.typography.caption1,
                color = MaterialTheme.colors.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Heart Rate Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "$heartRate BPM",
                    style = MaterialTheme.typography.title1,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                    fontSize = 18.sp
                )
                Text(
                    text = "Heart Rate",
                    style = MaterialTheme.typography.caption2,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }

        // --- 3. Circular Action Buttons Layer (Top Layer) ---
        CircularActionButtons(
            onNavigateToScreen = onNavigateToScreen,
            enabledComplications = settings.enabledComplications
                .filter {
                    it != com.example.wear.data.settings.ComplicationFeature.HISTORY &&
                            it != com.example.wear.data.settings.ComplicationFeature.MAINTENANCE
                }
                .toList(),
            modifier = Modifier.fillMaxSize()
        )
    }
}
@Composable
private fun AnalogWatchFace(
    currentTime: Date,
    modifier: Modifier = Modifier,
    primaryColor: Color,
    onBackground: Color,
    onSurface: Color
) {
    val calendar = Calendar.getInstance().apply { time = currentTime }
    val seconds = calendar.get(Calendar.SECOND)
    val minutes = calendar.get(Calendar.MINUTE)
    val hours = calendar.get(Calendar.HOUR)

    // Calculate angles
    val secondAngle = seconds * 6f - 90f
    val minuteAngle = (minutes * 6f + seconds * 0.1f) - 90f
    val hourAngle = (hours % 12 * 30f + minutes * 0.5f) - 90f

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        // 1. Draw Hour Markers
        for (i in 0 until 12) {
            val angle = i * 30f
            val angleRad = Math.toRadians(angle.toDouble() - 90) // Adjust for 12 at top
            val markerLength = if (i % 3 == 0) 10f else 5f // Longer for 12, 3, 6, 9
            val markerWidth = if (i % 3 == 0) 3f else 1f

            val startRadius = radius * 0.9f
            val endRadius = startRadius + markerLength

            val startX = center.x + startRadius * cos(angleRad).toFloat()
            val startY = center.y + startRadius * sin(angleRad).toFloat()
            val endX = center.x + endRadius * cos(angleRad).toFloat()
            val endY = center.y + endRadius * sin(angleRad).toFloat()

            drawLine(
                color = onSurface.copy(alpha = 0.8f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = markerWidth
            )
        }

        // 2. Draw Hour Hand
        val hourRadius = radius * 0.4f
        val hourX = center.x + hourRadius * cos(Math.toRadians(hourAngle.toDouble())).toFloat()
        val hourY = center.y + hourRadius * sin(Math.toRadians(hourAngle.toDouble())).toFloat()
        drawLine(
            color = onBackground,
            start = center,
            end = Offset(hourX, hourY),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // 3. Draw Minute Hand
        val minuteRadius = radius * 0.65f
        val minuteX = center.x + minuteRadius * cos(Math.toRadians(minuteAngle.toDouble())).toFloat()
        val minuteY = center.y + minuteRadius * sin(Math.toRadians(minuteAngle.toDouble())).toFloat()
        drawLine(
            color = onBackground,
            start = center,
            end = Offset(minuteX, minuteY),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // 4. Draw Second Hand (Primary/Accent Color)
        val secondRadius = radius * 0.8f
        val secondX = center.x + secondRadius * cos(Math.toRadians(secondAngle.toDouble())).toFloat()
        val secondY = center.y + secondRadius * sin(Math.toRadians(secondAngle.toDouble())).toFloat()
        drawLine(
            color = primaryColor,
            start = center,
            end = Offset(secondX, secondY),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // 5. Draw Center Dot
        drawCircle(
            color = primaryColor,
            radius = 6.dp.toPx(),
            center = center
        )
    }
}
@Composable
fun CircularActionButtons(
    onNavigateToScreen: (String) -> Unit,
    enabledComplications: List<com.example.wear.data.settings.ComplicationFeature>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenSize = configuration.screenWidthDp.dp
    val buttonSize = 30.dp
    val radius = (screenSize / 2) - (buttonSize / 2) - 4.dp

    // Filter out history and maintenance, then sort remaining complications
    val filteredComplications = enabledComplications
        .filter { 
            it != com.example.wear.data.settings.ComplicationFeature.HISTORY &&
            it != com.example.wear.data.settings.ComplicationFeature.MAINTENANCE
        }
        .sortedBy { complication ->
            when (complication) {
                com.example.wear.data.settings.ComplicationFeature.SETTINGS -> 0
                com.example.wear.data.settings.ComplicationFeature.ALL_MEDS -> 1
                com.example.wear.data.settings.ComplicationFeature.EMERGENCY -> 2
                com.example.wear.data.settings.ComplicationFeature.VITALS -> 3
                com.example.wear.data.settings.ComplicationFeature.UPCOMING -> 4
                else -> 5 // Handle any unexpected cases
            }
        }

    // Calculate angles for symmetrical placement
    val actions = filteredComplications.mapIndexed { index, complication ->
        // Distribute buttons symmetrically around the circle
        val totalButtons = filteredComplications.size
        val angle = when (totalButtons) {
            1 -> 90f // Single button at top
            2 -> if (index == 0) 45f else 315f // Top-right and top-left
            3 -> when (index) { // Top-right, top-left, and bottom
                0 -> 30f
                1 -> 150f
                else -> 270f
            }
            4 -> when (index) { // Top-right, top-left, bottom-left, bottom-right
                0 -> 45f
                1 -> 135f
                2 -> 225f
                else -> 315f
            }
            5 -> when (index) { // Evenly distributed
                0 -> 18f
                1 -> 90f
                2 -> 162f
                3 -> 234f
                else -> 306f
            }
            else -> { // Default to even distribution
                val step = 360f / totalButtons
                (step * index) % 360f
            }
        }

        val icon = when (complication) {
            com.example.wear.data.settings.ComplicationFeature.SETTINGS -> Icons.Default.Settings
            com.example.wear.data.settings.ComplicationFeature.ALL_MEDS -> Icons.Default.HealthAndSafety
            com.example.wear.data.settings.ComplicationFeature.EMERGENCY -> Icons.Default.Warning
            com.example.wear.data.settings.ComplicationFeature.VITALS -> Icons.Default.Favorite
            com.example.wear.data.settings.ComplicationFeature.UPCOMING -> Icons.Default.Notifications
            else -> Icons.Default.Info // Fallback icon
        }

        ActionButton(complication.displayName, icon, angle)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        actions.forEach { action ->
            val angleRad = Math.toRadians(action.angle.toDouble())
            val x = (radius.value * cos(angleRad)).dp
            val y = (radius.value * sin(angleRad)).dp

            Box(
                modifier = Modifier
                    .offset(x = x, y = y)
                    .size(buttonSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.1f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.primary,
                        shape = CircleShape
                    )
                    .clickable { onNavigateToScreen(action.title) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

data class ActionButton(
    val title: String,
    val icon: ImageVector,
    val angle: Float
)