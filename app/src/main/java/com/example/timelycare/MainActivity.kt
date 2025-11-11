package com.example.timelycare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timelycare.ui.theme.*
import com.example.timelycare.data.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimelyCareTheme {
                TimelyCareApp()
            }
        }
    }
}

@Composable
fun TimelyCareApp() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddMedicine by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<Medication?>(null) }

    Scaffold(
        topBar = {
            when {
                showAddMedicine -> AddMedicineHeader(
                    isEditing = editingMedication != null,
                    onBackClick = {
                        showAddMedicine = false
                        editingMedication = null
                    }
                )
                selectedTabIndex == 1 -> MedicationsHeader(onAddClick = { showAddMedicine = true })
                else -> TimelyCareTopBar()
            }
        },
        bottomBar = {
            if (!showAddMedicine) {
                TimelyCareBottomNavigation(
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
            }
        },
        containerColor = TimelyCareBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                showAddMedicine -> AddMedicineScreenContent(
                    editingMedication = editingMedication,
                    onSave = {
                        showAddMedicine = false
                        editingMedication = null
                    }
                )
                selectedTabIndex == 0 -> DashboardScreen()
                selectedTabIndex == 1 -> MedicationsScreen(
                    onAddClick = { showAddMedicine = true },
                    onEditClick = { medication ->
                        editingMedication = medication
                        showAddMedicine = true
                    }
                )
                selectedTabIndex == 2 -> CalendarScreen()
                selectedTabIndex == 3 -> ContactsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelyCareTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "TimelyCare",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareWhite
            )
        },
        actions = {
            IconButton(onClick = { /* Settings clicked */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TimelyCareWhite
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(TimelyCareBlue, TimelyCareBlueLight)
                )
            )
    )
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun TimelyCareBottomNavigation(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Dashboard", Icons.Default.Home),
        BottomNavItem("Medications", Icons.Default.Add),
        BottomNavItem("Calendar", Icons.Default.DateRange),
        BottomNavItem("Contacts", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = TimelyCareWhite,
        contentColor = TimelyCareGray
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TimelyCareBlue,
                    selectedTextColor = TimelyCareBlue,
                    unselectedIconColor = TimelyCareGray,
                    unselectedTextColor = TimelyCareGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun DashboardScreen() {
    val repository = remember { MedicationRepository.getInstance() }
    val medications by repository.medications.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Today's Schedule",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TimelyCareTextPrimary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (medications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No medications scheduled for today",
                    fontSize = 16.sp,
                    color = TimelyCareTextSecondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medications) { medication ->
                    TodayMedicationCard(medication = medication)
                }
            }
        }
    }
}

@Composable
fun TodayMedicationCard(medication: Medication) {
    var isTaken by remember { mutableStateOf(false) } // For demo purposes
    val borderColor = if (isTaken) Color(0xFF4CAF50) else Color.Transparent
    val firstTime = medication.medicationTimes.firstOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (isTaken) BorderStroke(2.dp, borderColor) else null
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Top row with pill icon, name/dosage, and time/status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side with pill icon and medication info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pill icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = TimelyCareBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simple pill representation using overlapping circles
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Pill body (rectangle with rounded ends)
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(8.dp)
                                    .background(
                                        color = TimelyCareBlue,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            // Pill cap (top half)
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height(8.dp)
                                    .background(
                                        color = TimelyCareBlue.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .offset(x = (-5).dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Medicine name and dosage
                    Column {
                        Text(
                            text = medication.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TimelyCareTextPrimary
                        )
                        Text(
                            text = medication.dosage,
                            fontSize = 16.sp,
                            color = TimelyCareTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Right side with time and status
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Time",
                            tint = TimelyCareTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = firstTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "No time",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TimelyCareTextPrimary
                        )
                    }
                    Text(
                        text = if (isTaken) "Taken" else "Upcoming",
                        fontSize = 14.sp,
                        color = if (isTaken) Color(0xFF4CAF50) else TimelyCareTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom action button
            Button(
                onClick = { isTaken = !isTaken },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTaken) Color(0xFF4CAF50) else TimelyCareBlue
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                if (isTaken) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Taken",
                        tint = TimelyCareWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Taken",
                        color = TimelyCareWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Mark as Taken",
                        color = TimelyCareWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MedicationsScreen(onAddClick: () -> Unit, onEditClick: (Medication) -> Unit) {
    val repository = remember { MedicationRepository.getInstance() }
    val medications by repository.medications.collectAsStateWithLifecycle()

    if (medications.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "No medications added yet.",
                    fontSize = 16.sp,
                    color = TimelyCareTextSecondary
                )

                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TimelyCareBlue
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Add Medicine",
                        color = TimelyCareWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medications) { medication ->
                    MedicationCard(
                        medication = medication,
                        onEdit = { onEditClick(medication) },
                        onDelete = { repository.deleteMedication(medication.id) }
                    )
                }
            }

            // Add Medicine Button at bottom
            Button(
                onClick = onAddClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TimelyCareBlue
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Add Medicine",
                    color = TimelyCareWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MedicationCard(
    medication: Medication,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareTextPrimary
                    )
                    Text(
                        text = medication.dosage,
                        fontSize = 16.sp,
                        color = TimelyCareTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    if (medication.medicationTimes.isNotEmpty()) {
                        Text(
                            text = "Times: ${medication.medicationTimes.joinToString(", ") {
                                it.format(DateTimeFormatter.ofPattern("hh:mm a"))
                            }}",
                            fontSize = 14.sp,
                            color = TimelyCareTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Edit Button
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.size(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(1.dp, TimelyCareBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TimelyCareBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Delete Button
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.size(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(1.dp, Color(0xFFF44336)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsHeader(onAddClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "My Medications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareWhite
            )
        },
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Medication",
                    tint = TimelyCareWhite
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(TimelyCareBlue, TimelyCareBlueLight)
                )
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineHeader(isEditing: Boolean = false, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = if (isEditing) "Edit Medicine" else "Add Medicine",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TimelyCareWhite
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TimelyCareWhite
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(TimelyCareBlue, TimelyCareBlueLight)
                )
            )
    )
}

@Composable
fun CalendarScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Calendar Screen",
            fontSize = 18.sp,
            color = TimelyCareTextPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Medicine",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TimelyCareWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(TimelyCareBlue, TimelyCareBlueLight)
                        )
                    )
            )
        },
        containerColor = TimelyCareBackground
    ) { paddingValues ->
        AddMedicineScreenContent(
            onSave = onBackClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreenContent(
    editingMedication: Medication? = null,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { MedicationRepository.getInstance() }

    // Helper functions to convert medication data back to form format
    fun formatDateToString(date: LocalDate?): String {
        return date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: ""
    }

    fun formatTimeToString(time: LocalTime): String {
        return time.format(DateTimeFormatter.ofPattern("h:mm a"))
    }

    fun formatFrequencyToString(freq: Frequency): String {
        return when (freq) {
            is Frequency.Daily -> "Daily"
            is Frequency.SpecificDays -> {
                if (freq.days.size == 7) "Daily"
                else freq.days.joinToString(", ")
            }
        }
    }

    // Form field states - initialize with editing medication data if available
    var medicineName by remember { mutableStateOf(editingMedication?.name ?: "") }
    var dosage by remember { mutableStateOf(editingMedication?.dosage ?: "") }
    var selectedType by remember { mutableStateOf(editingMedication?.type?.toString() ?: "Pill") }
    var frequency by remember { mutableStateOf(editingMedication?.let { formatFrequencyToString(it.frequency) } ?: "Daily") }
    var startDate by remember { mutableStateOf(formatDateToString(editingMedication?.startDate)) }
    var endDate by remember { mutableStateOf(formatDateToString(editingMedication?.endDate)) }
    var medicationTimes by remember {
        mutableStateOf(
            editingMedication?.medicationTimes?.map { formatTimeToString(it) }
                ?: listOf("08:00 AM", "01:00 PM", "08:00 PM")
        )
    }
    var specialInstructions by remember { mutableStateOf(editingMedication?.specialInstructions ?: "") }
    var showFrequencyModal by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var editingTimeIndex by remember { mutableIntStateOf(-1) }
    var isAddingNewTime by remember { mutableStateOf(false) }

    // Validation states
    var medicineNameError by remember { mutableStateOf("") }
    var dosageError by remember { mutableStateOf("") }
    var timesError by remember { mutableStateOf("") }

    fun validateForm(): Boolean {
        medicineNameError = if (medicineName.isBlank()) "Medicine name is required" else ""
        dosageError = if (dosage.isBlank()) "Dosage is required" else ""
        timesError = if (medicationTimes.isEmpty()) "At least one medication time is required" else ""

        return medicineNameError.isEmpty() && dosageError.isEmpty() && timesError.isEmpty()
    }

    fun parseTimeString(timeStr: String): LocalTime? {
        return try {
            LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("h:mm a"))
        } catch (e: DateTimeParseException) {
            try {
                LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e2: DateTimeParseException) {
                null
            }
        }
    }

    fun parseDateString(dateStr: String): LocalDate? {
        return try {
            if (dateStr.isBlank()) null
            else LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun parseFrequency(frequencyStr: String): Frequency {
        return if (frequencyStr == "Daily") {
            Frequency.Daily
        } else {
            val dayStrings = frequencyStr.split(", ")
            val days = dayStrings.mapNotNull { dayStr ->
                when (dayStr.trim()) {
                    "Mon" -> DayOfWeek.MONDAY
                    "Tue" -> DayOfWeek.TUESDAY
                    "Wed" -> DayOfWeek.WEDNESDAY
                    "Thu" -> DayOfWeek.THURSDAY
                    "Fri" -> DayOfWeek.FRIDAY
                    "Sat" -> DayOfWeek.SATURDAY
                    "Sun" -> DayOfWeek.SUNDAY
                    else -> null
                }
            }.toSet()
            if (days.isEmpty()) Frequency.Daily else Frequency.SpecificDays(days)
        }
    }

    fun formatDisplayDate(dateStr: String): String {
        return if (dateStr.isBlank()) {
            "Select Date"
        } else {
            try {
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
            } catch (e: DateTimeParseException) {
                "Select Date"
            }
        }
    }

    fun formatTimeFromHourMinute(hour: Int, minute: Int): String {
        val localTime = LocalTime.of(hour, minute)
        return localTime.format(DateTimeFormatter.ofPattern("h:mm a"))
    }

    fun parseTimeToHourMinute(timeStr: String): Pair<Int, Int> {
        return try {
            val localTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("h:mm a"))
            Pair(localTime.hour, localTime.minute)
        } catch (e: DateTimeParseException) {
            Pair(9, 0) // Default to 9:00 AM
        }
    }

    fun sortMedicationTimes(times: List<String>): List<String> {
        return times.sortedBy { timeStr ->
            try {
                LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("h:mm a"))
            } catch (e: DateTimeParseException) {
                LocalTime.of(9, 0) // Default sort position
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = medicineName,
            onValueChange = {
                medicineName = it
                if (medicineNameError.isNotEmpty()) medicineNameError = ""
            },
            label = { Text("Medicine Name *") },
            placeholder = { Text("e.g., Amoxicillin") },
            isError = medicineNameError.isNotEmpty(),
            supportingText = if (medicineNameError.isNotEmpty()) {
                { Text(medicineNameError, color = Color.Red) }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TimelyCareBlue,
                focusedLabelColor = TimelyCareBlue
            )
        )

        OutlinedTextField(
            value = dosage,
            onValueChange = {
                dosage = it
                if (dosageError.isNotEmpty()) dosageError = ""
            },
            label = { Text("Dosage *") },
            placeholder = { Text("e.g., 500 mg") },
            isError = dosageError.isNotEmpty(),
            supportingText = if (dosageError.isNotEmpty()) {
                { Text(dosageError, color = Color.Red) }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TimelyCareBlue,
                focusedLabelColor = TimelyCareBlue
            )
        )

        Text(
            text = "Type *",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = typeDropdownExpanded,
            onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TimelyCareBlue,
                    focusedLabelColor = TimelyCareBlue
                )
            )
            ExposedDropdownMenu(
                expanded = typeDropdownExpanded,
                onDismissRequest = { typeDropdownExpanded = false }
            ) {
                listOf("Pill", "Tablet", "Capsule", "Liquid", "Injection", "Others").forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            typeDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Frequency *",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = { showFrequencyModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TimelyCareBlue
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = frequency,
                color = TimelyCareWhite
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Start Date",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TimelyCareGray)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDisplayDate(startDate),
                            color = if (startDate.isBlank()) TimelyCareGray else TimelyCareTextPrimary,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select start date",
                            tint = TimelyCareBlue
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "End Date",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TimelyCareGray)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDisplayDate(endDate),
                            color = if (endDate.isBlank()) TimelyCareGray else TimelyCareTextPrimary,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select end date",
                            tint = TimelyCareBlue
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Medication Times *",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        medicationTimes.forEachIndexed { index, time ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp
                )

                IconButton(
                    onClick = {
                        editingTimeIndex = index
                        isAddingNewTime = false
                        showTimePicker = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit time",
                        tint = TimelyCareBlue
                    )
                }

                Button(
                    onClick = {
                        medicationTimes = medicationTimes.filterIndexed { i, _ -> i != index }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    ),
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "Remove",
                        color = TimelyCareWhite,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Button(
            onClick = {
                editingTimeIndex = -1
                isAddingNewTime = true
                showTimePicker = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Add Time",
                color = TimelyCareWhite,
                fontSize = 16.sp
            )
        }

        Text(
            text = "Special Instructions",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = specialInstructions,
            onValueChange = { specialInstructions = it },
            placeholder = { Text("Enter any special instructions...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(bottom = 24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TimelyCareBlue,
                focusedLabelColor = TimelyCareBlue
            ),
            maxLines = 4
        )

        if (timesError.isNotEmpty()) {
            Text(
                text = timesError,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (validateForm()) {
                    val parsedTimes = medicationTimes.mapNotNull { parseTimeString(it) }
                    val medication = if (editingMedication != null) {
                        // Update existing medication
                        editingMedication.copy(
                            name = medicineName.trim(),
                            dosage = dosage.trim(),
                            type = MedicationType.fromString(selectedType),
                            frequency = parseFrequency(frequency),
                            startDate = parseDateString(startDate),
                            endDate = parseDateString(endDate),
                            medicationTimes = parsedTimes,
                            specialInstructions = specialInstructions.trim()
                        )
                    } else {
                        // Create new medication
                        Medication(
                            name = medicineName.trim(),
                            dosage = dosage.trim(),
                            type = MedicationType.fromString(selectedType),
                            frequency = parseFrequency(frequency),
                            startDate = parseDateString(startDate),
                            endDate = parseDateString(endDate),
                            medicationTimes = parsedTimes,
                            specialInstructions = specialInstructions.trim()
                        )
                    }

                    if (editingMedication != null) {
                        repository.updateMedication(medication)
                    } else {
                        repository.addMedication(medication)
                    }
                    onSave()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TimelyCareBlue
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (editingMedication != null) "Update Medicine" else "Add Medicine",
                color = TimelyCareWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (showFrequencyModal) {
        FrequencySelectionModal(
            currentFrequency = frequency,
            onDismiss = { showFrequencyModal = false },
            onFrequencySelected = { frequency = it }
        )
    }

    // Start Date Picker
    if (showStartDatePicker) {
        val startDatePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = LocalDate.ofEpochDay(millis / 86400000L)
                            startDate = selectedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK", color = TimelyCareBlue, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartDatePicker = false }
                ) {
                    Text("Cancel", color = TimelyCareGray, fontSize = 16.sp)
                }
            }
        ) {
            DatePicker(
                state = startDatePickerState,
                title = {
                    Text(
                        text = "Select Start Date",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }

    // End Date Picker
    if (showEndDatePicker) {
        val endDatePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = LocalDate.ofEpochDay(millis / 86400000L)
                            endDate = selectedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK", color = TimelyCareBlue, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndDatePicker = false }
                ) {
                    Text("Cancel", color = TimelyCareGray, fontSize = 16.sp)
                }
            }
        ) {
            DatePicker(
                state = endDatePickerState,
                title = {
                    Text(
                        text = "Select End Date",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val currentTime = if (editingTimeIndex >= 0 && editingTimeIndex < medicationTimes.size) {
            parseTimeToHourMinute(medicationTimes[editingTimeIndex])
        } else {
            Pair(9, 0) // Default to 9:00 AM for new times
        }

        var hourText by remember { mutableStateOf(if (currentTime.first > 12) (currentTime.first - 12).toString() else if (currentTime.first == 0) "12" else currentTime.first.toString()) }
        var minuteText by remember { mutableStateOf(String.format("%02d", currentTime.second)) }
        var isAM by remember { mutableStateOf(currentTime.first < 12) }

        var hourError by remember { mutableStateOf("") }
        var minuteError by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = {
                Text(
                    text = if (isAddingNewTime) "Add Medication Time" else "Edit Medication Time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Enter Time",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Hour and Minute Input Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        // Hour Field
                        Column {
                            Text(
                                text = "Hour",
                                fontSize = 14.sp,
                                color = TimelyCareTextSecondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = hourText,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                        hourText = newValue
                                        hourError = when {
                                            newValue.isEmpty() -> "Required"
                                            newValue.toIntOrNull() == null -> "Invalid"
                                            newValue.toInt() < 1 || newValue.toInt() > 12 -> "1-12 only"
                                            else -> ""
                                        }
                                    }
                                },
                                modifier = Modifier.width(80.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = hourError.isNotEmpty(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TimelyCareBlue,
                                    focusedLabelColor = TimelyCareBlue
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        // Colon Separator
                        Text(
                            text = ":",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TimelyCareTextPrimary,
                            modifier = Modifier.padding(top = 20.dp)
                        )

                        // Minute Field
                        Column {
                            Text(
                                text = "Minute",
                                fontSize = 14.sp,
                                color = TimelyCareTextSecondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = minuteText,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                        minuteText = newValue
                                        minuteError = when {
                                            newValue.isEmpty() -> "Required"
                                            newValue.toIntOrNull() == null -> "Invalid"
                                            newValue.toInt() < 0 || newValue.toInt() > 59 -> "0-59 only"
                                            else -> ""
                                        }
                                    }
                                },
                                modifier = Modifier.width(80.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = minuteError.isNotEmpty(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TimelyCareBlue,
                                    focusedLabelColor = TimelyCareBlue
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }

                    // Error Messages
                    if (hourError.isNotEmpty() || minuteError.isNotEmpty()) {
                        Text(
                            text = when {
                                hourError.isNotEmpty() -> "Hour: $hourError"
                                minuteError.isNotEmpty() -> "Minute: $minuteError"
                                else -> ""
                            },
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // AM/PM Radio Buttons
                    Text(
                        text = "Time of Day",
                        fontSize = 14.sp,
                        color = TimelyCareTextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        // AM Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = isAM,
                                    onClick = { isAM = true }
                                )
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = isAM,
                                onClick = { isAM = true },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = TimelyCareBlue,
                                    unselectedColor = TimelyCareGray
                                )
                            )
                            Text(
                                text = "AM",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // PM Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = !isAM,
                                    onClick = { isAM = false }
                                )
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = !isAM,
                                onClick = { isAM = false },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = TimelyCareBlue,
                                    unselectedColor = TimelyCareGray
                                )
                            )
                            Text(
                                text = "PM",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Validate inputs
                        val hour = hourText.toIntOrNull()
                        val minute = minuteText.toIntOrNull()

                        if (hour != null && minute != null &&
                            hour in 1..12 && minute in 0..59 &&
                            hourError.isEmpty() && minuteError.isEmpty()) {

                            // Convert to 24-hour format
                            val hour24 = when {
                                hour == 12 && isAM -> 0
                                hour == 12 && !isAM -> 12
                                isAM -> hour
                                else -> hour + 12
                            }

                            val newTimeStr = formatTimeFromHourMinute(hour24, minute)

                            if (isAddingNewTime) {
                                // Check for duplicates
                                if (!medicationTimes.contains(newTimeStr)) {
                                    medicationTimes = sortMedicationTimes(medicationTimes + newTimeStr)
                                }
                            } else if (editingTimeIndex >= 0 && editingTimeIndex < medicationTimes.size) {
                                // Update existing time
                                val updatedTimes = medicationTimes.toMutableList()
                                updatedTimes[editingTimeIndex] = newTimeStr
                                medicationTimes = sortMedicationTimes(updatedTimes)
                            }

                            showTimePicker = false
                            editingTimeIndex = -1
                            isAddingNewTime = false
                        }
                    }
                ) {
                    Text(
                        "OK",
                        color = TimelyCareBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                        editingTimeIndex = -1
                        isAddingNewTime = false
                    }
                ) {
                    Text(
                        "Cancel",
                        color = TimelyCareGray,
                        fontSize = 16.sp
                    )
                }
            }
        )
    }
}

@Composable
fun FrequencySelectionModal(
    currentFrequency: String,
    onDismiss: () -> Unit,
    onFrequencySelected: (String) -> Unit
) {
    fun parseFrequencyString(frequencyStr: String): Set<DayOfWeek> {
        return if (frequencyStr == "Daily") {
            setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        } else {
            val dayStrings = frequencyStr.split(", ")
            dayStrings.mapNotNull { dayStr ->
                when (dayStr.trim()) {
                    "Mon" -> DayOfWeek.MONDAY
                    "Tue" -> DayOfWeek.TUESDAY
                    "Wed" -> DayOfWeek.WEDNESDAY
                    "Thu" -> DayOfWeek.THURSDAY
                    "Fri" -> DayOfWeek.FRIDAY
                    "Sat" -> DayOfWeek.SATURDAY
                    "Sun" -> DayOfWeek.SUNDAY
                    else -> null
                }
            }.toSet()
        }
    }

    var selectedDays by remember { mutableStateOf(parseFrequencyString(currentFrequency)) }
    val isAllDaysSelected = selectedDays.size == 7

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TimelyCareWhite)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Frequency",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    onClick = {
                        selectedDays = if (isAllDaysSelected) {
                            emptySet()
                        } else {
                            setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAllDaysSelected) TimelyCareBlue.copy(alpha = 0.1f) else Color.Transparent
                    ),
                    border = BorderStroke(2.dp, if (isAllDaysSelected) TimelyCareBlue else TimelyCareGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isAllDaysSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = TimelyCareBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = "Daily",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY).forEach { dayOfWeek ->
                        val isSelected = selectedDays.contains(dayOfWeek)
                        Button(
                            onClick = {
                                selectedDays = if (isSelected) {
                                    selectedDays - dayOfWeek
                                } else {
                                    selectedDays + dayOfWeek
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TimelyCareBlue else Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = if (!isSelected) BorderStroke(1.dp, TimelyCareGray) else null,
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(
                                text = dayOfWeek.toString(),
                                color = if (isSelected) TimelyCareWhite else TimelyCareGray,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).forEach { dayOfWeek ->
                        val isSelected = selectedDays.contains(dayOfWeek)
                        Button(
                            onClick = {
                                selectedDays = if (isSelected) {
                                    selectedDays - dayOfWeek
                                } else {
                                    selectedDays + dayOfWeek
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TimelyCareBlue else Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = if (!isSelected) BorderStroke(1.dp, TimelyCareGray) else null,
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(
                                text = dayOfWeek.toString(),
                                color = if (isSelected) TimelyCareWhite else TimelyCareGray,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val frequencyString = if (isAllDaysSelected) {
                            "Daily"
                        } else {
                            selectedDays.joinToString(", ")
                        }
                        onFrequencySelected(frequencyString)
                        onDismiss()
                    },
                    enabled = selectedDays.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TimelyCareBlue,
                        disabledContainerColor = TimelyCareGray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Done",
                        color = if (selectedDays.isNotEmpty()) TimelyCareWhite else TimelyCareGray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ContactsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Contacts Screen",
            fontSize = 18.sp,
            color = TimelyCareTextPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimelyCareAppPreview() {
    TimelyCareTheme {
        TimelyCareApp()
    }
}