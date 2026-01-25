package com.example.wear.data.analytics

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Sealed class representing different types of analytics events
 */
sealed class AnalyticsEvent {
    abstract val timestamp: Long
    abstract val sessionId: String

    /**
     * Records when a session starts
     */
    data class SessionStart(
        override val timestamp: Long,
        override val sessionId: String
    ) : AnalyticsEvent()

    /**
     * Records when a session ends with total duration
     */
    data class SessionEnd(
        override val timestamp: Long,
        override val sessionId: String,
        val totalDurationMs: Long
    ) : AnalyticsEvent()

    /**
     * Records screen view with screen name and optional duration
     * Duration is populated when exiting the screen
     */
    data class ScreenView(
        override val timestamp: Long,
        override val sessionId: String,
        val screenName: String,
        val durationMs: Long? = null
    ) : AnalyticsEvent()

    /**
     * Records UI element interaction (tap/click)
     */
    data class ElementTap(
        override val timestamp: Long,
        override val sessionId: String,
        val screenName: String,
        val elementName: String,
        val elementType: String
    ) : AnalyticsEvent()
}

/**
 * Represents an active analytics session
 */
data class AnalyticsSession(
    val sessionId: String = UUID.randomUUID().toString().substring(0, 8),
    val startTime: Long = System.currentTimeMillis(),
    var currentScreen: String? = null,
    var currentScreenEntryTime: Long? = null
) {
    /**
     * Calculate total session duration
     */
    fun getTotalDuration(): Long = System.currentTimeMillis() - startTime
}

/**
 * Represents a row in the CSV export
 */
data class AnalyticsCSVRow(
    val timestamp: Long,
    val sessionId: String,
    val eventType: String,
    val screen: String,
    val element: String,
    val durationMs: String
) {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

        /**
         * CSV header row with human-readable columns
         */
        fun getHeader(): String = "date_time,session_id,event_type,screen,element,duration_seconds"

        /**
         * Convert AnalyticsEvent to CSV row
         */
        fun fromEvent(event: AnalyticsEvent): AnalyticsCSVRow {
            return when (event) {
                is AnalyticsEvent.SessionStart -> AnalyticsCSVRow(
                    timestamp = event.timestamp,
                    sessionId = event.sessionId,
                    eventType = "session_start",
                    screen = "",
                    element = "",
                    durationMs = ""
                )

                is AnalyticsEvent.SessionEnd -> AnalyticsCSVRow(
                    timestamp = event.timestamp,
                    sessionId = event.sessionId,
                    eventType = "session_end",
                    screen = "",
                    element = "",
                    durationMs = event.totalDurationMs.toString()
                )

                is AnalyticsEvent.ScreenView -> AnalyticsCSVRow(
                    timestamp = event.timestamp,
                    sessionId = event.sessionId,
                    eventType = "screen_view",
                    screen = event.screenName,
                    element = "",
                    durationMs = event.durationMs?.toString() ?: ""
                )

                is AnalyticsEvent.ElementTap -> AnalyticsCSVRow(
                    timestamp = event.timestamp,
                    sessionId = event.sessionId,
                    eventType = "element_tap",
                    screen = event.screenName,
                    element = "${event.elementType}:${event.elementName}",
                    durationMs = ""
                )
            }
        }

        /**
         * Format duration in milliseconds to seconds with 2 decimal places
         */
        private fun formatDuration(durationMs: String): String {
            if (durationMs.isEmpty()) return ""
            return try {
                val ms = durationMs.toLong()
                String.format(Locale.US, "%.2f", ms / 1000.0)
            } catch (e: NumberFormatException) {
                durationMs
            }
        }
    }

    /**
     * Convert to CSV string with human-readable format
     */
    fun toCSVString(): String {
        val formattedTime = dateFormat.format(Date(timestamp))
        val formattedDuration = AnalyticsCSVRow.formatDuration(durationMs)
        return "$formattedTime,$sessionId,$eventType,$screen,$element,$formattedDuration"
    }
}

/**
 * Helper to format session ID and timestamp for filename
 */
fun AnalyticsSession.getFilename(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    val timestamp = dateFormat.format(Date(startTime))
    return "analytics_${sessionId}_$timestamp.csv"
}
