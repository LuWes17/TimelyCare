package com.example.timelycare.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Composable
fun TwoWeeksCalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    // Calculate the start of the first week (Sunday)
    val startOfFirstWeek = selectedDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))
    val firstWeekDates = (0..6).map { startOfFirstWeek.plusDays(it.toLong()) }
    val secondWeekDates = (7..13).map { startOfFirstWeek.plusDays(it.toLong()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // First week
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                firstWeekDates.forEach { date ->
                    TwoWeeksDayItem(
                        date = date,
                        isSelected = date == selectedDate,
                        onDateSelected = onDateSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Second week
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                secondWeekDates.forEach { date ->
                    TwoWeeksDayItem(
                        date = date,
                        isSelected = date == selectedDate,
                        onDateSelected = onDateSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TwoWeeksDayItem(
    date: LocalDate,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayName = date.format(DateTimeFormatter.ofPattern("EEE"))
    val dayNumber = date.dayOfMonth

    Column(
        modifier = modifier
            .clickable { onDateSelected(date) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName,
            fontSize = 12.sp,
            color = TimelyCareTextSecondary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSelected) TimelyCareTextPrimary else Color.Transparent
                )
                .clickable { onDateSelected(date) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayNumber.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) TimelyCareWhite else TimelyCareTextPrimary
            )
        }
    }
}