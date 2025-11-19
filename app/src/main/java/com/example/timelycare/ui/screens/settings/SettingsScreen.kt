package com.example.timelycare.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.composed
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.data.AlertLevelOption
import com.example.timelycare.data.LanguageOption
import com.example.timelycare.data.SettingsRepository
import com.example.timelycare.data.UserSettings
import com.example.timelycare.ui.theme.*

private data class ThemeColorOption(val color: Color, val label: String)
private data class AlertLevel(val label: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val languageOptions = listOf("English", "Filipino")

    // FIX: First color is a constant, unaffected by theme selection
    val themeColorOptions = listOf(
        ThemeColorOption(TimelyCareBlue, "Ocean Blue"),
        ThemeColorOption(Color(0xFFD64545), "Sunset Red"),
        ThemeColorOption(Color(0xFF2F9F5B), "Garden Green"),
        ThemeColorOption(Color(0xFF7A4EDB), "Royal Purple"),
        ThemeColorOption(Color(0xFFED873F), "Amber Glow"),
        ThemeColorOption(Color(0xFFE91E63), "Blush Pink")
    )

    val alertLevels = listOf(
        AlertLevel("Low", "Subtle vibration, fewer alerts"),
        AlertLevel("Normal", "Standard alerts"),
        AlertLevel("High", "Loud vibration, frequent alerts")
    )

    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository.getInstance(context) }
    val currentSettings by settingsRepository.settings.collectAsStateWithLifecycle()

    var selectedLanguage by rememberSaveable { mutableStateOf(languageOptions.first()) }
    var languageExpanded by remember { mutableStateOf(false) }
    var selectedThemeColor by remember {
        mutableStateOf(
            themeColorOptions[currentSettings.themeColorIndex.coerceIn(0, themeColorOptions.lastIndex)]
        )
    }
    var darkModeEnabled by rememberSaveable { mutableStateOf(currentSettings.darkModeEnabled) }
    var selectedAlertLevel by remember {
        mutableStateOf(
            when (currentSettings.alertLevel) {
                AlertLevelOption.LOW -> alertLevels[0]
                AlertLevelOption.HIGH -> alertLevels[2]
                else -> alertLevels[1]
            }
        )
    }
    var alertExpanded by remember { mutableStateOf(false) }
    var saveConfirmation by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(saveConfirmation) {
        if (saveConfirmation) {
            kotlinx.coroutines.delay(2000)
            saveConfirmation = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimelyCareWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TimelyCareWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TimelyCareBlue)
            )
        },
        containerColor = TimelyCareBackground
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /** LANGUAGE CARD **/
            SettingsCard {
                val langLabel =
                    if (currentSettings.language == LanguageOption.FILIPINO) "Wika" else "Language"
                val preferredLabel =
                    if (currentSettings.language == LanguageOption.FILIPINO) "Napiling wika" else "Preferred language"

                Text(langLabel, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = languageExpanded,
                    onExpandedChange = { languageExpanded = !languageExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(preferredLabel) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = languageExpanded
                            )
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TimelyCareBlue,
                            unfocusedBorderColor = TimelyCareGray
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false }
                    ) {
                        languageOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedLanguage = option
                                    languageExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            /** THEME COLOR CARD — FIXED **/
            SettingsCard {
                Text("Theme Color", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    themeColorOptions.forEach { option ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(option.color)   // FIX: Show correct static color
                                .border(
                                    width = if (option == selectedThemeColor) 3.dp else 1.dp,
                                    color = if (option == selectedThemeColor)
                                        Color.Black.copy(alpha = 0.6f)
                                    else
                                        Color.White.copy(alpha = 0.6f),
                                    shape = CircleShape
                                )
                                .clickableWithoutRipple { selectedThemeColor = option }
                        )
                    }
                }
            }

            /** DARK MODE CARD **/
            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Mode", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Switch between light and dark theme",
                            fontSize = 14.sp,
                            color = TimelyCareTextSecondary
                        )
                    }

                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TimelyCareWhite,
                            checkedTrackColor = TimelyCareBlue
                        )
                    )
                }
            }

            /** ALERT LEVEL CARD **/
            SettingsCard {
                Text("Alert Sensitivity", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = alertExpanded,
                    onExpandedChange = { alertExpanded = !alertExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAlertLevel.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Notification level") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = alertExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TimelyCareBlue,
                            unfocusedBorderColor = TimelyCareGray
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = alertExpanded,
                        onDismissRequest = { alertExpanded = false }
                    ) {
                        alertLevels.forEach { level ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(level.label, fontWeight = FontWeight.SemiBold)
                                        Text(level.description, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    selectedAlertLevel = level
                                    alertExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(selectedAlertLevel.description, fontSize = 14.sp)
            }

            /** SAVE BUTTON **/
            Button(
                onClick = {
                    val languageOption =
                        if (selectedLanguage == "Filipino") LanguageOption.FILIPINO
                        else LanguageOption.ENGLISH

                    val alertOption = when (selectedAlertLevel.label) {
                        "Low" -> AlertLevelOption.LOW
                        "High" -> AlertLevelOption.HIGH
                        else -> AlertLevelOption.NORMAL
                    }

                    val themeIndex =
                        themeColorOptions.indexOf(selectedThemeColor).coerceAtLeast(0)

                    settingsRepository.updateSettings(
                        UserSettings(
                            language = languageOption,
                            themeColorIndex = themeIndex,
                            darkModeEnabled = darkModeEnabled,
                            alertLevel = alertOption
                        )
                    )

                    saveConfirmation = true
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TimelyCareBlue)
            ) {
                Text(
                    "Save Settings",
                    color = TimelyCareWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (saveConfirmation) {
                Text(
                    "Settings saved successfully!",
                    color = TimelyCareBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }
}
