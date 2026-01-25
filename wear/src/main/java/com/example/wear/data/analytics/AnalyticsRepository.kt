package com.example.wear.data.analytics

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileWriter

/**
 * Repository for managing analytics data collection and export
 * Singleton pattern with thread-safe initialization
 */
class AnalyticsRepository private constructor(private val context: Context) {

    companion object {
        private const val TAG = "AnalyticsRepository"

        @Volatile
        private var INSTANCE: AnalyticsRepository? = null

        fun getInstance(context: Context): AnalyticsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AnalyticsRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // State management
    private val _currentSession = MutableStateFlow<AnalyticsSession?>(null)
    val currentSession: StateFlow<AnalyticsSession?> = _currentSession.asStateFlow()

    private val _isCollecting = MutableStateFlow(false)
    val isCollecting: StateFlow<Boolean> = _isCollecting.asStateFlow()

    // In-memory event buffer
    private val events = mutableListOf<AnalyticsEvent>()

    /**
     * Start a new analytics session
     */
    fun startSession() {
        if (_isCollecting.value) {
            Log.w(TAG, "Session already active, ignoring startSession call")
            return
        }

        val session = AnalyticsSession()
        _currentSession.value = session
        _isCollecting.value = true

        // Record session start event
        val startEvent = AnalyticsEvent.SessionStart(
            timestamp = System.currentTimeMillis(),
            sessionId = session.sessionId
        )
        events.add(startEvent)

        Log.d(TAG, "Session started: ${session.sessionId}")
    }

    /**
     * End the current session and export data to CSV
     * Returns the filename if successful, null otherwise
     */
    fun endSession(): String? {
        val session = _currentSession.value
        if (session == null || !_isCollecting.value) {
            Log.w(TAG, "No active session to end")
            return null
        }

        // Record session end event
        val endEvent = AnalyticsEvent.SessionEnd(
            timestamp = System.currentTimeMillis(),
            sessionId = session.sessionId,
            totalDurationMs = session.getTotalDuration()
        )
        events.add(endEvent)

        // Export to CSV
        val filename = exportToCSV(session)

        // Clear state
        _currentSession.value = null
        _isCollecting.value = false
        events.clear()

        Log.d(TAG, "Session ended: ${session.sessionId}, exported to: $filename")
        return filename
    }

    /**
     * Record a screen view event
     * Automatically closes previous screen if one is active
     */
    fun recordScreenView(screenName: String) {
        val session = _currentSession.value
        if (session == null || !_isCollecting.value) {
            return
        }

        val currentTime = System.currentTimeMillis()

        // Close previous screen if active
        if (session.currentScreen != null && session.currentScreenEntryTime != null) {
            val duration = currentTime - session.currentScreenEntryTime!!
            val screenExitEvent = AnalyticsEvent.ScreenView(
                timestamp = session.currentScreenEntryTime!!,
                sessionId = session.sessionId,
                screenName = session.currentScreen!!,
                durationMs = duration
            )
            events.add(screenExitEvent)
            Log.d(TAG, "Screen exited: ${session.currentScreen}, duration: ${duration}ms")
        }

        // Record new screen entry
        session.currentScreen = screenName
        session.currentScreenEntryTime = currentTime

        // Note: We'll record this screen with duration when exiting or ending session
        Log.d(TAG, "Screen entered: $screenName")
    }

    /**
     * Record a UI element tap event
     */
    fun recordElementTap(elementName: String, screenName: String, elementType: String) {
        val session = _currentSession.value
        if (session == null || !_isCollecting.value) {
            return
        }

        val tapEvent = AnalyticsEvent.ElementTap(
            timestamp = System.currentTimeMillis(),
            sessionId = session.sessionId,
            screenName = screenName,
            elementName = elementName,
            elementType = elementType
        )
        events.add(tapEvent)

        Log.d(TAG, "Element tapped: $elementType:$elementName on $screenName")
    }

    /**
     * Export events to CSV file in external files directory
     * Returns the filename on success, null on failure
     */
    private fun exportToCSV(session: AnalyticsSession): String? {
        try {
            // Close current screen if active
            if (session.currentScreen != null && session.currentScreenEntryTime != null) {
                val duration = System.currentTimeMillis() - session.currentScreenEntryTime!!
                val screenExitEvent = AnalyticsEvent.ScreenView(
                    timestamp = session.currentScreenEntryTime!!,
                    sessionId = session.sessionId,
                    screenName = session.currentScreen!!,
                    durationMs = duration
                )
                events.add(screenExitEvent)
            }

            // Get external files directory (accessible via ADB)
            val externalFilesDir = context.getExternalFilesDir(null)
            if (externalFilesDir == null) {
                Log.e(TAG, "External files directory not available")
                return null
            }

            // Create CSV file
            val filename = session.getFilename()
            val file = File(externalFilesDir, filename)

            FileWriter(file).use { writer ->
                // Write header
                writer.append(AnalyticsCSVRow.getHeader())
                writer.append("\n")

                // Write events
                events.forEach { event ->
                    val csvRow = AnalyticsCSVRow.fromEvent(event)
                    writer.append(csvRow.toCSVString())
                    writer.append("\n")
                }
            }

            Log.d(TAG, "CSV exported successfully: ${file.absolutePath}")
            Log.d(TAG, "Total events: ${events.size}")
            return filename
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export CSV", e)
            return null
        }
    }

    /**
     * Get the current event count (for debugging)
     */
    fun getEventCount(): Int = events.size

    /**
     * Force clear all data (for testing/debugging)
     */
    fun clearAll() {
        _currentSession.value = null
        _isCollecting.value = false
        events.clear()
        Log.d(TAG, "All analytics data cleared")
    }
}
