package com.example.timelycare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.DisposableEffect
import com.example.timelycare.data.SettingsRepository
import com.example.timelycare.ui.theme.TimelyCareTheme
import com.example.timelycare.ui.navigation.TimelyCareApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsRepository = SettingsRepository.getInstance(this)
        settingsRepository.onLanguageChanged = {
            recreate()
        }

        setContent {
            TimelyCareTheme {
                TimelyCareApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimelyCareAppPreview() {
    TimelyCareTheme {
        TimelyCareApp()
    }
}