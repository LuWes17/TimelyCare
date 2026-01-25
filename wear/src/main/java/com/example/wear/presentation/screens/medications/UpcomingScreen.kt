package com.example.wear.presentation.screens.medications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.material.*
import com.example.wear.MedicationRepository
import com.example.wear.presentation.components.TrackableButton
import com.example.wear.presentation.components.rememberAnalyticsTracker
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UpcomingScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val medicationRepository = remember { MedicationRepository.getInstance(context) }
    val medications by medicationRepository.medications.collectAsState()
    val takenRecords by medicationRepository.takenRecords.collectAsState()
    val listState = rememberScalingLazyListState()

    // Filter to show only medications that haven't been taken today
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormatter.format(Date())

    val upcomingMedications = medications.filter { medication ->
        !medicationRepository.isMedicationTaken(medication.id, medication.time, today)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        if (upcomingMedications.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = "No upcoming medications",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "All Caught Up!",
                    style = MaterialTheme.typography.title2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No upcoming medications for today",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            ScalingLazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 45.dp), // Consistent with AllMedicationsScreen
                anchorType = ScalingLazyListAnchorType.ItemStart,
                contentPadding = PaddingValues(
                    top = 12.dp,
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 0.dp // Consistent with AllMedicationsScreen
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Reduced spacing
            ) {
                item {
                    Text(
                        text = "Upcoming Medications",
                        style = MaterialTheme.typography.title2,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                items(upcomingMedications) { medication ->
                    UpcomingMedicationCard(
                        medication = medication,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Floating Back Button - Consistent with AllMedicationsScreen
        TrackableButton(
            elementName = "Back",
            screenName = "UPCOMING",
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
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
private fun UpcomingMedicationCard(
    medication: com.example.wear.Medication,
    modifier: Modifier = Modifier
) {
    val trackEvent = rememberAnalyticsTracker()

    Card(
        onClick = {
            trackEvent("UpcomingMedicationCard_${medication.name}", "UPCOMING", "card")
            /* TODO: Add medication action if needed */
        },
        modifier = modifier.padding(horizontal = 4.dp),
        enabled = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp), // Reduced padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = "Medication",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(24.dp) // Smaller icon
            )

            Spacer(modifier = Modifier.height(4.dp)) // Reduced spacing

            Text(
                text = medication.name,
                style = MaterialTheme.typography.title3,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(2.dp)) // Reduced spacing

            Text(
                text = "Dosage: ${medication.dosage}",
                style = MaterialTheme.typography.caption1, // Smaller text
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp)) // Reduced spacing

            Text(
                text = medication.time,
                style = MaterialTheme.typography.caption1, // Smaller text
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            if (medication.isMaintenanceMed) {
                Spacer(modifier = Modifier.height(4.dp)) // Reduced spacing

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)) // Smaller corners
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 2.dp) // Reduced padding
                ) {
                    Text(
                        text = "Maintenance",
                        style = MaterialTheme.typography.caption2, // Smallest text
                        color = MaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}