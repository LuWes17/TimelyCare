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
import java.time.temporal.TemporalAdjusters

@Composable
fun MonthCalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val firstDayOfMonth = selectedDate.withDayOfMonth(1)
    val lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth())

    // Calculate the start of calendar (first Sunday of the calendar grid)
    val firstSunday = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))

    // Calculate the end of calendar (last Saturday of the calendar grid)
    val lastSaturday = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY))

    // Generate all dates to display using manual iteration for compatibility
    val totalDays = mutableListOf<LocalDate>()
    var currentDate = firstSunday
    while (!currentDate.isAfter(lastSaturday)) {
        totalDays.add(currentDate)
        currentDate = currentDate.plusDays(1)
    }
    val weeks = totalDays.chunked(7)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            weeks.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { date ->
                        MonthDayItem(
                            date = date,
                            isSelected = date == selectedDate,
                            isCurrentMonth = date.month == selectedDate.month,
                            isToday = date == today,
                            onDateSelected = onDateSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (week != weeks.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MonthDayItem(
    date: LocalDate,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNumber = date.dayOfMonth

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onDateSelected(date) }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    when {
                        isSelected -> TimelyCareTextPrimary
                        isToday -> TimelyCareBlue.copy(alpha = 0.2f)
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayNumber.toString(),
                fontSize = 16.sp,
                fontWeight = when {
                    isSelected -> FontWeight.Bold
                    isToday -> FontWeight.Bold
                    else -> FontWeight.Normal
                },
                color = when {
                    isSelected -> TimelyCareWhite
                    !isCurrentMonth -> TimelyCareGray.copy(alpha = 0.5f)
                    isToday -> TimelyCareBlue
                    else -> TimelyCareTextPrimary
                }
            )
        }
    }
}