package com.example.wear.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.wear.data.analytics.AnalyticsRepository
import kotlinx.coroutines.delay

/**
 * Analytics overlay that shows START/END button at top of screen
 * Green "START" button when inactive, red "END" button when collecting
 */
@Composable
fun AnalyticsOverlay() {
    val context = LocalContext.current
    val repository = remember { AnalyticsRepository.getInstance(context) }
    val isCollecting by repository.isCollecting.collectAsState()

    var showSuccessMessage by remember { mutableStateOf(false) }
    var successFilename by remember { mutableStateOf("") }

    // Show success message for 3 seconds after ending session
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            delay(3000)
            showSuccessMessage = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // START/END Button
            Button(
                onClick = {
                    if (isCollecting) {
                        // End session
                        val filename = repository.endSession()
                        if (filename != null) {
                            successFilename = filename
                            showSuccessMessage = true
                        }
                    } else {
                        // Start session
                        repository.startSession()
                    }
                },
                modifier = Modifier
                    .height(32.dp)
                    .widthIn(min = 50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isCollecting) Color(0xFFDC143C) else Color(0xFF00C853)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isCollecting) "END" else "START",
                    color = Color.White,
                    style = androidx.wear.compose.material.MaterialTheme.typography.caption1,
                    fontWeight = FontWeight.Bold
                )
            }

            // Small indicator when collecting
            if (isCollecting) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = Color(0xFFDC143C), shape = CircleShape)
                )
            }

            // Success message
            if (showSuccessMessage) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(
                            color = Color(0xFF00C853).copy(alpha = 0.9f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✓ Exported",
                            color = Color.White,
                            style = androidx.wear.compose.material.MaterialTheme.typography.caption1
                        )
                        Text(
                            text = successFilename,
                            color = Color.White,
                            style = androidx.wear.compose.material.MaterialTheme.typography.caption2
                        )
                    }
                }
            }
        }
    }
}
