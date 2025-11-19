package com.example.wear.presentation.screens.vitals

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.launch
import com.example.wear.presentation.screens.common.SimpleGraph

enum class VitalScreen {
    OVERVIEW, HEART_RATE, BLOOD_PRESSURE, BLOOD_GLUCOSE
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VitalsScreen(
    onHeartClick: () -> Unit,
    onTempClick: () -> Unit,
    onBpClick: () -> Unit,
    onGlucoseClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val vitalsRepository = rememberVitalsRepository()
    val vitalsOverview by vitalsRepository.vitalsOverview.collectAsState()
    val heartRate by vitalsRepository.heartRate.collectAsState()
    val bloodPressure by vitalsRepository.bloodPressure.collectAsState()
    val bloodGlucose by vitalsRepository.bloodGlucose.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> VitalsOverviewPage(vitalsOverview)
                1 -> HeartRatePage(heartRate)
                2 -> BloodPressurePage(bloodPressure)
                3 -> BloodGlucosePage(bloodGlucose)
            }
        }

        // Left arrow (only show if not on first page)
        if (pagerState.currentPage > 0) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous",
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 20.dp)
                    .size(24.dp)
            )
        }

        // Right arrow (only show if not on last page)
        if (pagerState.currentPage < 3) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-20).dp)
                    .size(24.dp)
            )
        }

        // Floating Back Button - Consistent with AllMedicationsScreen
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun VitalsOverviewPage(vitalsOverview: VitalsOverview) {
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        anchorType = ScalingLazyListAnchorType.ItemStart,
        contentPadding = PaddingValues(
            top = 0.dp,
            bottom = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Vitals",
                style = MaterialTheme.typography.title2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        item {
            VitalCard(
                title = "Heart Rate",
                value = "${vitalsOverview.heartRate.bpm}",
                unit = "BPM",
                icon = Icons.Default.Favorite
            )
        }

        item {
            VitalCard(
                title = "Blood Pressure",
                value = "${vitalsOverview.bloodPressure.systolic}/${vitalsOverview.bloodPressure.diastolic}",
                unit = "mmHg",
                icon = Icons.Default.MonitorHeart
            )
        }

        item {
            VitalCard(
                title = "Blood Glucose",
                value = "${vitalsOverview.bloodGlucose.level}",
                unit = "mg/dL",
                icon = Icons.Default.Bloodtype
            )
        }
    }
}

@Composable
fun HeartRatePage(heartRate: HeartRateData) {
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
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Heart Rate",
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${heartRate.bpm}",
            style = MaterialTheme.typography.display1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface,
            fontSize = 32.sp
        )

        Text(
            text = "Beats per minute",
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Simple trend line
        SimpleGraph(
            data = heartRate.graphPoints,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
    }
}

@Composable
fun BloodPressurePage(bloodPressure: BloodPressureData) {
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
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Blood Pressure",
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${bloodPressure.systolic}",
                style = MaterialTheme.typography.title1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                fontSize = 28.sp
            )
            Text(
                text = "/",
                style = MaterialTheme.typography.title1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                fontSize = 28.sp
            )
            Text(
                text = "${bloodPressure.diastolic}",
                style = MaterialTheme.typography.title1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                fontSize = 28.sp
            )
        }

        Text(
            text = "mmHg",
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Simple trend line
        SimpleGraph(
            data = bloodPressure.systolicPoints,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
    }
}

@Composable
fun BloodGlucosePage(bloodGlucose: BloodGlucoseData) {
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
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Blood Glucose",
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${bloodGlucose.level}",
            style = MaterialTheme.typography.display1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface,
            fontSize = 32.sp
        )

        Text(
            text = "mg/dL",
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Simple trend line
        SimpleGraph(
            data = bloodGlucose.graphPoints,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
    }
}

@Composable
fun VitalCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        onClick = { /* Handle card click if needed */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.title1,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}