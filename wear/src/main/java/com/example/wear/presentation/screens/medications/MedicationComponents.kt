package com.example.wear.presentation.screens.medications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.example.wear.Medication

@Composable
fun UpcomingMedicationCard(
    medication: Medication,
    isMaintenanceMed: Boolean,
    scheduledTime: String,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* Future: mark as taken functionality */ },
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Health icon
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = "Medication",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(24.dp)
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
                text = medication.dosage,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Time
            Text(
                text = scheduledTime,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            // Maintenance Badge
            if (isMaintenanceMed) {
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Maintenance",
                        style = MaterialTheme.typography.caption1,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MaintenanceMedicationCard(
    medication: Medication,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* Future: show medication details */ },
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Maintenance",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${medication.dosage} • ${medication.medicationTimes.joinToString(", ")}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = medication.frequency,
                    style = MaterialTheme.typography.caption2,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Composable
fun HistoryMedicationCard(
    medicationName: String,
    dosage: String,
    takenTime: String,
    scheduledTime: String,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* Future: show details */ },
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkmark icon
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Taken",
                tint = Color.Green,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = medicationName,
                    style = MaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dosage,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Taken at $takenTime",
                    style = MaterialTheme.typography.caption2,
                    color = Color.Green
                )
                if (takenTime != scheduledTime) {
                    Text(
                        text = "Scheduled: $scheduledTime",
                        style = MaterialTheme.typography.caption2,
                        color = MaterialTheme.colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}