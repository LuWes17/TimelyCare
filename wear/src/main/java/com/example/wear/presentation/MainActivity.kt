/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.wear.Medication
import com.example.wear.MedicationRepository
import com.example.wear.presentation.theme.TimelyCareTheme
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp(this)
        }
    }
}

@Composable
fun WearApp(context: ComponentActivity) {
    val repository = remember { MedicationRepository(context) }
    val medications by repository.medications.collectAsState()

    // DEVELOPMENT ONLY: Add test button for emulator testing
    var showTestData by remember { mutableStateOf(false) }

    TimelyCareTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            TimeText()

            val displayMedications = if (showTestData && medications.isEmpty()) {
                // Show sample data for testing
                listOf(
                    Medication("1", "Aspirin", "100mg", "08:00", "Daily"),
                    Medication("2", "Vitamins", "1 tablet", "09:00", "Daily"),
                    Medication("3", "Blood Pressure", "50mg", "20:00", "Daily")
                )
            } else {
                medications
            }

            if (displayMedications.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EmptyState()
                    Spacer(modifier = Modifier.height(16.dp))
                    // Development test button
                    Button(
                        onClick = { showTestData = true },
                        modifier = Modifier.size(80.dp, 32.dp)
                    ) {
                        Text("Test", style = MaterialTheme.typography.caption1)
                    }
                }
            } else {
                MedicationList(
                    medications = displayMedications,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 25.dp)
                )
            }
        }
    }
}

@Composable
fun MedicationList(
    medications: List<Medication>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text(
                text = "Medications",
                style = MaterialTheme.typography.title3,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(medications) { medication ->
            MedicationCard(medication = medication)
        }
    }
}

@Composable
fun MedicationCard(medication: Medication) {
    Card(
        onClick = { /* Future: show medication details */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface
            )
            Text(
                text = "${medication.dosage} • ${medication.time}",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurfaceVariant
            )
            Text(
                text = medication.frequency,
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TimelyCare",
            style = MaterialTheme.typography.title2,
            color = MaterialTheme.colors.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No medications synced",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Use phone app to sync",
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    val sampleMedications = listOf(
        Medication("1", "Aspirin", "100mg", "08:00", "Daily"),
        Medication("2", "Vitamins", "1 tablet", "09:00", "Daily")
    )

    TimelyCareTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            TimeText()
            MedicationList(
                medications = sampleMedications,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp)
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun EmptyStatePreview() {
    TimelyCareTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            TimeText()
            EmptyState(modifier = Modifier.align(Alignment.Center))
        }
    }
}