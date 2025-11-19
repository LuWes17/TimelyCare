package com.example.wear.presentation.screens.emergency

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.material.*
import com.example.wear.EmergencyContact
import com.example.wear.MedicationRepository
import kotlinx.coroutines.delay

@Composable
fun EmergencyScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val medicationRepository = remember { MedicationRepository.getInstance(context) }
    val emergencyContacts by medicationRepository.emergencyContacts.collectAsState()
    val listState = rememberScalingLazyListState()

    var callMessage by remember { mutableStateOf<String?>(null) }
    var fallAlertState by remember { mutableStateOf(FallAlertState.NONE) }

    val primaryContact = medicationRepository.getPrimaryContact()
    val backupContacts = medicationRepository.getBackupContacts()

    if (fallAlertState == FallAlertState.NONE) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            TimeText()

            ScalingLazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 45.dp),
                anchorType = ScalingLazyListAnchorType.ItemStart,
                contentPadding = PaddingValues(
                    top = 12.dp,
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 0.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Emergency",
                        style = MaterialTheme.typography.title1,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = "Primary Contact",
                        style = MaterialTheme.typography.title2,
                        color = MaterialTheme.colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    primaryContact?.let { contact ->
                        Button(
                            onClick = {
                                callMessage = "Calling ${contact.name} through your phone"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(horizontal = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Red
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = contact.name,
                                        style = MaterialTheme.typography.title2,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = contact.relationship,
                                        style = MaterialTheme.typography.body2,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    } ?: run {
                        Text(
                            text = "No primary contact set",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (backupContacts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Backup Contacts",
                            style = MaterialTheme.typography.title2,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        )
                    }

                    items(backupContacts) { contact ->
                        EmergencyContactListItem(
                            contact = contact,
                            onCallClick = {
                                callMessage = "Calling ${contact.name} through your phone"
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Card(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(top = 16.dp)
                            .border(
                                2.dp,
                                MaterialTheme.colors.primary,
                                RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = androidx . compose . ui . Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Fall Detection",
                                    tint = MaterialTheme.colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Fall Detection",
                                    style = MaterialTheme.typography.title3,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.onSurface,
                                    maxLines = 2
                                )
                            }
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            fallAlertState = FallAlertState.DETECTED
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .border(
                            width = 2.dp,
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(24.dp)
                        ),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.surface
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "Simulate Fall (Demo)",
                            style = MaterialTheme.typography.button,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }

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

    when (fallAlertState) {
        FallAlertState.DETECTED -> {
            EmergencyFallAlertScreen(
                onSendAlertClick = {
                    fallAlertState = FallAlertState.SENT
                },
                onCancelClick = {
                    fallAlertState = FallAlertState.NONE
                }
            )
        }
        FallAlertState.SENT -> {
            EmergencyAlertSentScreen(
                onDismiss = {
                    fallAlertState = FallAlertState.NONE
                }
            )
        }
        FallAlertState.NONE -> {
        }
    }

    callMessage?.let { message ->
        LaunchedEffect(message) {
            delay(3000)
            callMessage = null
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                onClick = { },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}