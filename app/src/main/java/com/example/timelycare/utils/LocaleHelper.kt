package com.example.timelycare.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.example.timelycare.data.LanguageOption
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, languageOption: LanguageOption): Context {
        val localeCode = when (languageOption) {
            LanguageOption.FILIPINO -> "fil"
            LanguageOption.ENGLISH -> "en"
        }

        val locale = Locale(localeCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun updateResources(context: Context, languageOption: LanguageOption) {
        val localeCode = when (languageOption) {
            LanguageOption.FILIPINO -> "fil"
            LanguageOption.ENGLISH -> "en"
        }

        val locale = Locale(localeCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun getCurrentLocale(context: Context): Locale {
        return context.resources.configuration.locales[0]
    }

    fun getLanguageOptionFromLocale(locale: Locale): LanguageOption {
        return when (locale.language) {
            "fil" -> LanguageOption.FILIPINO
            else -> LanguageOption.ENGLISH
        }
    }
}