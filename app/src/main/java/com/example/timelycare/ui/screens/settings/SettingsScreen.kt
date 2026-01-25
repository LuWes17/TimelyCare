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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timelycare.R
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
    modifier: Modifier = Modifier
) {
    val languageOptions = listOf(
        stringResource(R.string.english),
        stringResource(R.string.filipino)
    )

    val themeColorOptions = listOf(
        ThemeColorOption(Color(0xFF00C853), stringResource(R.string.green)),
        ThemeColorOption(Color(0xFF2196F3), stringResource(R.string.blue)),
        ThemeColorOption(Color(0xFF9C27B0), stringResource(R.string.purple)),
        ThemeColorOption(Color(0xFFF44336), stringResource(R.string.red)),
        ThemeColorOption(Color(0xFFFF9800), stringResource(R.string.orange)),
        ThemeColorOption(Color(0xFFE91E63), stringResource(R.string.pink))
    )

    val alertLevels = listOf(
        AlertLevel(
            stringResource(R.string.alert_level_low),
            stringResource(R.string.alert_level_low_description)
        ),
        AlertLevel(
            stringResource(R.string.alert_level_normal),
            stringResource(R.string.alert_level_normal_description)
        ),
        AlertLevel(
            stringResource(R.string.alert_level_high),
            stringResource(R.string.alert_level_high_description)
        )
    )

    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository.getInstance(context) }
    val currentSettings by settingsRepository.settings.collectAsStateWithLifecycle()

    var selectedLanguage by rememberSaveable { mutableStateOf(languageOptions.first()) }
    var languageExpanded by remember { mutableStateOf(false) }

    var selectedThemeColor by remember {
        mutableStateOf(
            themeColorOptions[
                currentSettings.themeColorIndex.coerceIn(
                    0,
                    themeColorOptions.lastIndex
                )
            ]
        )
    }

    var darkModeEnabled by rememberSaveable {
        mutableStateOf(currentSettings.darkModeEnabled)
    }

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

    val englishText = stringResource(R.string.english)
    val filipinoText = stringResource(R.string.filipino)
    val alertLowText = stringResource(R.string.alert_level_low)
    val alertHighText = stringResource(R.string.alert_level_high)

    LaunchedEffect(currentSettings.language) {
        selectedLanguage = when (currentSettings.language) {
            LanguageOption.FILIPINO -> filipinoText
            else -> englishText
        }
    }

    LaunchedEffect(currentSettings.themeColorIndex) {
        selectedThemeColor = themeColorOptions[
            currentSettings.themeColorIndex.coerceIn(
                0,
                themeColorOptions.lastIndex
            )
        ]
    }

    LaunchedEffect(saveConfirmation) {
        if (saveConfirmation) {
            kotlinx.coroutines.delay(2000)
            saveConfirmation = false
        }
    }

    Scaffold(
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

            /* ---------- LANGUAGE ---------- */
            SettingsCard {
                Text(
                    stringResource(R.string.language),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TimelyCareTextPrimary
                )

                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = languageExpanded,
                    onExpandedChange = { languageExpanded = !languageExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.preferred_language)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = languageExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
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

            /* ---------- THEME COLOR ---------- */
            SettingsCard {
                Text(
                    stringResource(R.string.theme_color),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TimelyCareTextPrimary
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    themeColorOptions.forEach { option ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(option.color)
                                .border(
                                    width = if (option == selectedThemeColor) 3.dp else 1.dp,
                                    color = if (option == selectedThemeColor)
                                        TimelyCareBlueDark
                                    else Color.White.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .clickableWithoutRipple {
                                    selectedThemeColor = option
                                }
                        )
                    }
                }
            }

            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            stringResource(R.string.dark_mode),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TimelyCareTextPrimary
                        )
                        Text(
                            stringResource(R.string.dark_mode_description),
                            fontSize = 14.sp,
                            color = TimelyCareTextSecondary
                        )
                    }

                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = TimelyCareBlue,
                            checkedThumbColor = TimelyCareWhite
                        )
                    )
                }
            }

            /* ---------- ALERT LEVEL ---------- */
            SettingsCard {
                Text(
                    stringResource(R.string.alert_sensitivity),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TimelyCareTextPrimary
                )

                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = alertExpanded,
                    onExpandedChange = { alertExpanded = !alertExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAlertLevel.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.notification_level)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = alertExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
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
                                        Text(
                                            level.description,
                                            fontSize = 12.sp,
                                            color = TimelyCareTextSecondary
                                        )
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
            }

            /* ---------- SAVE ---------- */
            Button(
                onClick = {
                    settingsRepository.updateSettings(
                        UserSettings(
                            language = if (selectedLanguage == filipinoText)
                                LanguageOption.FILIPINO
                            else LanguageOption.ENGLISH,
                            themeColorIndex = themeColorOptions.indexOf(selectedThemeColor),
                            darkModeEnabled = darkModeEnabled,
                            alertLevel = when (selectedAlertLevel.label) {
                                alertLowText -> AlertLevelOption.LOW
                                alertHighText -> AlertLevelOption.HIGH
                                else -> AlertLevelOption.NORMAL
                            }
                        )
                    )
                    saveConfirmation = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TimelyCareBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    stringResource(R.string.save_settings),
                    color = TimelyCareWhite,
                    fontSize = 18.sp
                )
            }

            if (saveConfirmation) {
                Text(
                    stringResource(R.string.settings_saved_successfully),
                    color = TimelyCareBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TimelyCareWhite),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

private fun Modifier.clickableWithoutRipple(
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }
}
