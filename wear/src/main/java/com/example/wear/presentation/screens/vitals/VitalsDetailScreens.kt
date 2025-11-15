package com.example.wear.presentation.screens.vitals

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import com.example.wear.presentation.screens.common.SimpleGraph

@Composable
fun AllVitalsScreen() {
    val vitals = listOf(
        Triple("Heart Rate", "68 BPM", Icons.Default.Favorite),
        Triple("Blood Pressure", "120/80", Icons.Default.MonitorHeart),
        Triple("Blood Glucose", "95 mg/dL", Icons.Default.Bloodtype),
        Triple("Temperature", "98.6°F", Icons.Default.Thermostat)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(42.dp))

        Text(
            text = "All Vitals",
            style = MaterialTheme.typography.title1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vitals.chunked(2)) { vitalPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    vitalPair.forEach { (title, value, icon) ->
                        VitalCard(
                            title = title,
                            value = value.split(" ")[0],
                            unit = value.split(" ").drop(1).joinToString(" "),
                            icon = icon,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // If odd number, add spacer for the missing card
                    if (vitalPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Swipe indicator
        Text(
            text = "← Swipe for details →",
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun HeartRateScreen() {
    val heartRateData = remember { HeartRateData(68) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(42.dp))

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Heart Rate",
            tint = Color.Red,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Heart Rate",
            style = MaterialTheme.typography.title2,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${heartRateData.bpm}",
            style = MaterialTheme.typography.display1,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            fontSize = 42.sp
        )

        Text(
            text = "BPM",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Heart rate graph
        Card(
            onClick = { /* Nothing to do on click */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Last 7 readings",
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                SimpleGraph(
                    data = heartRateData.graphPoints,
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Normal range: 60-100 BPM",
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BloodPressureScreen() {
    val bloodPressureData = remember { BloodPressureData(120, 80) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(42.dp))

        Icon(
            imageVector = Icons.Default.MonitorHeart,
            contentDescription = "Blood Pressure",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Blood Pressure",
            style = MaterialTheme.typography.title2,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${bloodPressureData.systolic}",
                style = MaterialTheme.typography.display2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 36.sp
            )
            Text(
                text = "/",
                style = MaterialTheme.typography.display2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground,
                fontSize = 36.sp
            )
            Text(
                text = "${bloodPressureData.diastolic}",
                style = MaterialTheme.typography.display2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 36.sp
            )
        }

        Text(
            text = "mmHg",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Blood pressure trends
        Card(
            onClick = { /* Nothing to do on click */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Systolic trend",
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                SimpleGraph(
                    data = bloodPressureData.systolicPoints,
                    color = MaterialTheme.colors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Normal: <120/80 mmHg",
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BloodGlucoseScreen() {
    val bloodGlucoseData = remember { BloodGlucoseData(95) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(42.dp))

        Icon(
            imageVector = Icons.Default.Bloodtype,
            contentDescription = "Blood Glucose",
            tint = Color(0xFFFF9800),
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Blood Glucose",
            style = MaterialTheme.typography.title2,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${bloodGlucoseData.level}",
            style = MaterialTheme.typography.display1,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF9800),
            fontSize = 42.sp
        )

        Text(
            text = "mg/dL",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Glucose level graph
        Card(
            onClick = { /* Nothing to do on click */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Recent readings",
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                SimpleGraph(
                    data = bloodGlucoseData.graphPoints,
                    color = Color(0xFFFF9800)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Normal: 70-100 mg/dL",
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}