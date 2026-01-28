package com.example.timelycare.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.Medication
import com.example.timelycare.data.MedicationRepository
import com.example.timelycare.ui.theme.TimelyCareTextPrimary
import com.example.timelycare.ui.theme.TimelyCareTextSecondary
import java.time.LocalTime

@Composable
fun DashboardScreen(
    onHealthMetricClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val medicationRepository = remember { MedicationRepository.getInstance(context) }
    val medications by medicationRepository.medications.collectAsStateWithLifecycle()

    val sortedMedicationTimes = medications
        .flatMap { medication ->
            medication.medicationTimes.map { time -> medication to time }
        }
        .sortedWith(
            compareBy<Pair<Medication, LocalTime>> { it.second }
                .thenBy { it.first.name }
        )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {

        // Health Metrics Section
        item {
            HealthMetricsSection(
                onMetricClick = onHealthMetricClick
            )
        }

        // Title
        item {
            Text(
                text = "Today's Schedule",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareTextPrimary
            )
        }

        // Empty state
        if (sortedMedicationTimes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No medications scheduled for today",
                        fontSize = 16.sp,
                        color = TimelyCareTextSecondary
                    )
                }
            }
        } else {
            // Medication cards
            items(sortedMedicationTimes) { (medication, time) ->
                TodayMedicationCard(
                    medication = medication,
                    scheduledTime = time
                )
            }
        }
    }
}
