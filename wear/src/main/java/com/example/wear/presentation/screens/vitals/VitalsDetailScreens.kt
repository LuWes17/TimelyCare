package com.example.wear.presentation.screens.vitals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.wear.presentation.screens.common.SimpleGraph

@Composable
fun HeartRateDetailScreen(
    onBackClick: () -> Unit
) {
    val vitalsRepository = rememberVitalsRepository()
    val heartRate by vitalsRepository.heartRate.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Circular white background
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Heart Rate",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Heart Rate",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${heartRate.bpm}",
                    style = MaterialTheme.typography.display1,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 36.sp
                )

                Text(
                    text = "Beats per minute",
                    style = MaterialTheme.typography.caption1,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Simple trend line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SimpleGraph(
                        data = heartRate.graphPoints,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }

        // Back button at bottom
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp)
                .size(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF4CAF50)
            ),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BloodPressureDetailScreen(
    onBackClick: () -> Unit
) {
    val vitalsRepository = rememberVitalsRepository()
    val bloodPressure by vitalsRepository.bloodPressure.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Circular white background
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MonitorHeart,
                    contentDescription = "Blood Pressure",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Blood Pressure",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${bloodPressure.systolic}",
                        style = MaterialTheme.typography.title1,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 30.sp
                    )
                    Text(
                        text = "/",
                        style = MaterialTheme.typography.title1,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 30.sp
                    )
                    Text(
                        text = "${bloodPressure.diastolic}",
                        style = MaterialTheme.typography.title1,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 30.sp
                    )
                }

                Text(
                    text = "mmHg",
                    style = MaterialTheme.typography.caption1,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Simple trend line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SimpleGraph(
                        data = bloodPressure.systolicPoints,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }

        // Back button at bottom
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp)
                .size(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF4CAF50)
            ),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BloodGlucoseDetailScreen(
    onBackClick: () -> Unit
) {
    val vitalsRepository = rememberVitalsRepository()
    val bloodGlucose by vitalsRepository.bloodGlucose.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Circular white background
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bloodtype,
                    contentDescription = "Blood Glucose",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Blood Glucose",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${bloodGlucose.level}",
                    style = MaterialTheme.typography.display1,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 36.sp
                )

                Text(
                    text = "mg/dL",
                    style = MaterialTheme.typography.caption1,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Simple trend line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SimpleGraph(
                        data = bloodGlucose.graphPoints,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }

        // Back button at bottom
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp)
                .size(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF4CAF50)
            ),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Legacy screens kept for compatibility but updated with circular design
@Composable
fun HeartRateScreen() {
    HeartRateDetailScreen(onBackClick = { /* No-op for standalone usage */ })
}

@Composable
fun BloodPressureScreen() {
    BloodPressureDetailScreen(onBackClick = { /* No-op for standalone usage */ })
}

@Composable
fun BloodGlucoseScreen() {
    BloodGlucoseDetailScreen(onBackClick = { /* No-op for standalone usage */ })
}

@Composable
fun AllVitalsScreen() {
    // This can now just be a reference to the main vitals screen
    VitalsScreen(
        onHeartClick = { },
        onTempClick = { },
        onBpClick = { },
        onGlucoseClick = { },
        onBackClick = { }
    )
}