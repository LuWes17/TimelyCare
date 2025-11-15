package com.example.wear.presentation.screens.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

@Composable
fun PlaceholderScreen(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        TimeText()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.title2,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00C853),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Coming Soon",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .size(120.dp, 40.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.caption1
                )
            }
        }
    }
}

@Composable
fun SimpleGraph(
    data: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        val width = size.width
        val height = size.height
        val pointWidth = width / (data.size - 1)

        if (data.size > 1) {
            val path = Path()

            val firstY = height * (1f - data.first())
            path.moveTo(0f, firstY)

            data.forEachIndexed { index, point ->
                if (index > 0) {
                    val x = pointWidth * index
                    val y = height * (1f - point)
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}