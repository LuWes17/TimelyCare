package com.example.wear.presentation.screens.medications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import com.example.wear.MedicationRepository
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val medicationRepository = remember { MedicationRepository.getInstance(context) }
    val medications by medicationRepository.medications.collectAsState()
    val takenRecords by medicationRepository.takenRecords.collectAsState()

    // Filter to show only taken medications from today
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormatter.format(Date())

    val todayTakenMeds = takenRecords.filter { record ->
        record.takenDate == today
    }.mapNotNull { record ->
        medications.find { it.id == record.medicationId }?.let { medication ->
            Triple(medication, record.takenTime, record.scheduledTime)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        if (todayTakenMeds.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No medications taken today",
                    style = MaterialTheme.typography.title2,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 48.dp,
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 60.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Today's Medications",
                        style = MaterialTheme.typography.title2,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                items(todayTakenMeds) { (medication, takenTime, scheduledTime) ->
                    HistoryMedicationCard(
                        medicationName = medication.name,
                        dosage = medication.dosage,
                        takenTime = takenTime,
                        scheduledTime = scheduledTime,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
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
            ),
            shape = CircleShape
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