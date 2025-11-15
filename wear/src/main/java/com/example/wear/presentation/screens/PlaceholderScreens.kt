package com.example.wear.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.example.wear.Medication
import com.example.wear.MedicationRepository
import kotlinx.coroutines.launch

// Data classes for vitals
data class HeartRateData(
    val bpm: Int,
    val graphPoints: List<Float> = listOf(0.4f, 0.3f, 0.5f, 0.2f, 0.6f, 0.4f, 0.3f)
)

data class BloodPressureData(
    val systolic: Int,
    val diastolic: Int,
    val systolicPoints: List<Float> = listOf(0.3f, 0.2f, 0.4f, 0.3f, 0.5f, 0.4f),
    val diastolicPoints: List<Float> = listOf(0.7f, 0.6f, 0.8f, 0.7f, 0.9f, 0.8f)
)

data class BloodGlucoseData(
    val level: Int,
    val unit: String = "mg/dL"
)

// Sample data
val sampleHeartRateData = HeartRateData(bpm = 68)
val sampleBloodPressureData = BloodPressureData(systolic = 118, diastolic = 80)
val sampleBloodGlucoseData = BloodGlucoseData(level = 94)

@Composable
fun PlaceholderScreen(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.title2,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00C853),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Coming Soon",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .size(120.dp, 40.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.caption1
                )
            }
        }
    }
}



@Composable
fun HistoryScreen(onBackClick: () -> Unit) {
    PlaceholderScreen("History", onBackClick)
}

@Composable
fun MaintenanceScreen(
    onBackClick: () -> Unit,
    medicationRepository: MedicationRepository
) {
    val allMedications by medicationRepository.medications.collectAsState()
    val maintenanceMedications = allMedications.filter { it.isMaintenanceMed }
    val listState = rememberScalingLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        if (maintenanceMedications.isEmpty()) {
            // Empty State
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "No maintenance medications",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No Maintenance Medications",
                    style = MaterialTheme.typography.title2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mark medications as maintenance from your phone app",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            // Maintenance Medication List
            ScalingLazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 32.dp,
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 60.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Maintenance Meds",
                        style = MaterialTheme.typography.title2,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                items(maintenanceMedications) { medication ->
                    MaintenanceMedicationCard(
                        medication = medication,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Floating Back Button
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .size(44.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EmergencyScreen(onBackClick: () -> Unit) {
    PlaceholderScreen("Emergency", onBackClick)
}

@Composable
fun VitalsScreen(onBackClick: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> AllVitalsScreen()
                1 -> HeartRateScreen()
                2 -> BloodPressureScreen()
                3 -> BloodGlucoseScreen()
            }
        }

        // Navigation Arrows (decorative - swipe gesture handles navigation)
        if (pagerState.currentPage > 0) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous",
                tint = MaterialTheme.colors.primary.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
        }

        if (pagerState.currentPage < 3) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = MaterialTheme.colors.primary.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(24.dp)
            )
        }

        // Back Button
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .size(44.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun UpcomingScreen(onBackClick: () -> Unit) {
    PlaceholderScreen("Upcoming", onBackClick)
}

@Composable
private fun MaintenanceMedicationCard(
    medication: Medication,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* Future: show medication details */ },
        modifier = modifier.padding(horizontal = 4.dp),
        enabled = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Refresh Icon for Maintenance
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Maintenance Medication",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Medication Name
            Text(
                text = medication.name,
                style = MaterialTheme.typography.title3,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Dosage
            Text(
                text = "Dosage: ${medication.dosage}",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Frequency (new field for maintenance screen)
            Text(
                text = "Frequency: ${medication.frequency}",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Time
            Text(
                text = "Time: ${medication.time}",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AllVitalsScreen() {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 32.dp,
            start = 8.dp,
            end = 8.dp,
            bottom = 60.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Vitals",
                style = MaterialTheme.typography.title1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        item {
            VitalCard(
                icon = Icons.Default.Favorite,
                title = "Heart Rate",
                value = "71",
                unit = "BPM",
                color = MaterialTheme.colors.primary
            )
        }

        item {
            VitalCard(
                icon = Icons.Default.Timeline,
                title = "Blood Pressure",
                value = "118/80",
                unit = "mmHg",
                color = MaterialTheme.colors.primary
            )
        }

        item {
            VitalCard(
                icon = Icons.Default.WaterDrop,
                title = "Blood Glucose",
                value = "94",
                unit = "mg/dL",
                color = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
private fun VitalCard(
    icon: ImageVector,
    title: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* Future: navigate to specific vital */ },
        modifier = modifier.fillMaxWidth(),
        enabled = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.title3,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.title2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = unit,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HeartRateScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Heart Rate",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Heart Rate",
            style = MaterialTheme.typography.title2,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "68",
            style = MaterialTheme.typography.display1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
            fontSize = 26.sp
        )

        Text(
            text = "Beats per minute",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Heart Rate Graph
        SimpleGraph(
            points = sampleHeartRateData.graphPoints,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(40.dp)
        )
    }
}

@Composable
private fun BloodPressureScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Timeline,
            contentDescription = "Blood Pressure",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Blood Pressure",
            style = MaterialTheme.typography.title2,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "118/80",
            style = MaterialTheme.typography.display1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
            fontSize = 26.sp
        )

        Text(
            text = "mmHg",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Blood Pressure Dual Graph
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(40.dp)
        ) {
            SimpleGraph(
                points = sampleBloodPressureData.systolicPoints,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxSize()
            )
            SimpleGraph(
                points = sampleBloodPressureData.diastolicPoints,
                color = MaterialTheme.colors.primary.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun BloodGlucoseScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = "Blood Glucose",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Blood Glucose",
            style = MaterialTheme.typography.title2,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "94",
            style = MaterialTheme.typography.display1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
            fontSize = 26.sp
        )

        Text(
            text = sampleBloodGlucoseData.unit,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Blood Glucose Graph
        SimpleGraph(
            points = listOf(0.5f, 0.4f, 0.6f, 0.3f, 0.7f, 0.5f, 0.4f),
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(40.dp)
        )
    }
}

@Composable
private fun SimpleGraph(
    points: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val pointWidth = width / (points.size - 1)

        if (points.size > 1) {
            val path = Path()

            // Start from first point
            val firstY = height * (1f - points.first())
            path.moveTo(0f, firstY)

            // Draw lines to other points
            points.forEachIndexed { index, point ->
                if (index > 0) {
                    val x = pointWidth * index
                    val y = height * (1f - point)
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}