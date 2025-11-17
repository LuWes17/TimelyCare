package com.example.timelycare.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Stable
data class UserSettings(
    val language: LanguageOption = LanguageOption.ENGLISH,
    val themeColorIndex: Int = 0,
    val darkModeEnabled: Boolean = false,
    val alertLevel: AlertLevelOption = AlertLevelOption.NORMAL
)

enum class LanguageOption { ENGLISH, FILIPINO }

enum class AlertLevelOption { LOW, NORMAL, HIGH }

class SettingsRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("timelycare_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        val languageString = prefs.getString("language", LanguageOption.ENGLISH.name) ?: LanguageOption.ENGLISH.name
        val themeColorIndex = prefs.getInt("theme_color_index", 0)
        val darkModeEnabled = prefs.getBoolean("dark_mode_enabled", false)
        val alertLevelString = prefs.getString("alert_level", AlertLevelOption.NORMAL.name) ?: AlertLevelOption.NORMAL.name

        return UserSettings(
            language = LanguageOption.valueOf(languageString),
            themeColorIndex = themeColorIndex,
            darkModeEnabled = darkModeEnabled,
            alertLevel = AlertLevelOption.valueOf(alertLevelString)
        )
    }

    fun updateSettings(update: UserSettings) {
        _settings.value = update
        prefs.edit()
            .putString("language", update.language.name)
            .putInt("theme_color_index", update.themeColorIndex)
            .putBoolean("dark_mode_enabled", update.darkModeEnabled)
            .putString("alert_level", update.alertLevel.name)
            .apply()
    }

    fun updateDarkMode(enabled: Boolean) {
        val current = _settings.value
        updateSettings(current.copy(darkModeEnabled = enabled))
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun getInstance(context: Context): SettingsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepository(context).also { INSTANCE = it }
            }
        }
    }
}


