package com.example.wear.presentation.screens.emergency

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay

@Composable
fun EmergencyFallAlertScreen(
    onSendAlertClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fall Detected!",
                style = MaterialTheme.typography.title2,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Alerting emergency contacts",
                style = MaterialTheme.typography.body2,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSendAlertClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Send Alert Now",
                    style = MaterialTheme.typography.caption1,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCancelClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 4.dp)
                    .border(1.dp, Color.White, RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "I'm OK - Cancel",
                    style = MaterialTheme.typography.caption1,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun EmergencyAlertSentScreen(
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3000) // Auto dismiss after 3 seconds
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Alert Sent",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Alert Sent!",
                style = MaterialTheme.typography.title1,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Emergency contacts notified",
                style = MaterialTheme.typography.body2,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}