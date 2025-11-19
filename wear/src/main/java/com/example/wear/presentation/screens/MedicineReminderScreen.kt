package com.example.wear.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MedicineReminderScreen(
    medicineName: String = "Biogesic",
    dosage: String = "50mg",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val currentTime = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }
    val currentDate = remember {
        SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Main circular container (transparent)
        Box(
            modifier = Modifier
            .size(280.dp)
            .clip(CircleShape)
            .background(Color(0xFFE5E5E5))
            .size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            // Left dismiss button (Red)
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .size(50.dp)
                    .offset(x = (-80).dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFFF5252)
                ),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Right confirm button (Green)
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .size(50.dp)
                    .offset(x = 80.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4CAF50)
                ),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Central white circle with content
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Pill icon with green background
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalPharmacy,
                            contentDescription = "Medicine",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // "Time to Take Your Medicine" text
                    Text(
                        text = "Time to Take\nYour Medicine",
                        style = MaterialTheme.typography.caption1,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Medicine name
                    Text(
                        text = medicineName,
                        style = MaterialTheme.typography.title3,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    // Dosage
                    Text(
                        text = dosage,
                        style = MaterialTheme.typography.caption1,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Date
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.caption2,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )

                    // Time
                    Text(
                        text = currentTime,
                        style = MaterialTheme.typography.title3,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}