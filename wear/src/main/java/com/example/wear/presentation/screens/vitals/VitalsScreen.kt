package com.example.wear.presentation.screens.vitals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

// --- CONFIGURATION CONSTANTS (Tuned for Visual Sector Look) ---
private val BUTTON_SIZE = 150.dp
private val DIAGONAL_OFFSET = 55.dp
private val BACK_BUTTON_SIZE = 70.dp
private val CORNER_RADIUS = 50.dp

// --- THE VITALS MENU SCREEN (Diagonal Layout) ---
@Composable
fun VitalsScreen(
    onHeartClick: () -> Unit,
    onTempClick: () -> Unit,
    onBpClick: () -> Unit,
    onGlucoseClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Setting the background explicitly to White for the light aesthetic
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {

        // 1. Heart button (Top-Left Quadrant)
        ThemeRadialButton(
            label = "Heart",
            icon = Icons.Default.Favorite,
            modifier = Modifier.offset(x = -DIAGONAL_OFFSET, y = -DIAGONAL_OFFSET),
            onClick = onHeartClick
        )

        // 2. Temperature button (Top-Right Quadrant)
        ThemeRadialButton(
            label = "Temp",
            icon = Icons.Default.Thermostat,
            modifier = Modifier.offset(x = DIAGONAL_OFFSET, y = -DIAGONAL_OFFSET),
            onClick = onTempClick
        )

        // 3. Blood Pressure button (Bottom-Left Quadrant)
        ThemeRadialButton(
            label = "BP",
            icon = Icons.Default.MonitorHeart,
            modifier = Modifier.offset(x = -DIAGONAL_OFFSET, y = DIAGONAL_OFFSET),
            onClick = onBpClick
        )

        // 4. Glucose button (Bottom-Right Quadrant)
        ThemeRadialButton(
            label = "Glucose",
            icon = Icons.Default.Insights,
            modifier = Modifier.offset(x = DIAGONAL_OFFSET, y = DIAGONAL_OFFSET),
            onClick = onGlucoseClick
        )

        // Center Back button
        ThemeBackCenterButton(onBackClick)
    }
}

// --- THEME-BASED COMPONENTS ---

@Composable
fun ThemeRadialButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Primary color is used for the icon's tint
    val iconColor = MaterialTheme.colors.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(BUTTON_SIZE)
            // No border
            .background(Color.Transparent, shape = RoundedCornerShape(CORNER_RADIUS))
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = iconColor // Themed Icon Color
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Text color set to DarkGray for visibility against the White background
            Text(text = label, color = Color.DarkGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun ThemeBackCenterButton(onClick: () -> Unit) {
    val borderColor = MaterialTheme.colors.primary

    Box(
        modifier = Modifier
            .size(BACK_BUTTON_SIZE)
            .background(
                color = MaterialTheme.colors.primary,
                shape = CircleShape
            )
            // Keeping the theme-colored border on the center button for visual pop
            .border(
                width = 3.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}