package com.example.wear.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.material.*

import com.example.wear.Medication
import com.example.wear.MedicationRepository

@Composable
fun AllMedicationsScreen(
    onBackClick: () -> Unit,
    medicationRepository: MedicationRepository
) {
    val medications by medicationRepository.medications.collectAsState()
    val listState = rememberScalingLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        if (medications.isEmpty()) {
            EmptyMedicationState()
        } else {
            ScalingLazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 45.dp), // ensures bottom space for back button
                anchorType = ScalingLazyListAnchorType.ItemStart,
                contentPadding = PaddingValues(
                    top = 12.dp,
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 0.dp // safe bottom padding
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Reduced spacing between cards
            ) {
                // Header
                item {
                    Text(
                        text = "All Medications",
                        style = MaterialTheme.typography.title2,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Medication items
                items(medications) { medication ->
                    MedicationCard(
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
private fun EmptyMedicationState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.HealthAndSafety,
            contentDescription = "No medications",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Medications",
            style = MaterialTheme.typography.title2,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add medications from your phone app",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun MedicationCard(
    medication: Medication,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* TODO: Add medication action if needed */ },
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