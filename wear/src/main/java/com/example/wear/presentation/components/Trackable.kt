package com.example.wear.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import com.example.wear.data.analytics.AnalyticsRepository

/**
 * Extension function for Modifier to track clicks on any clickable element
 * Records the tap event before executing the original onClick
 *
 * @param elementName Name of the element being tapped
 * @param screenName Name of the current screen
 * @param elementType Type of element (e.g., "button", "icon_button", "card", "selector")
 * @param onClick Original click handler
 */
@Composable
fun Modifier.trackableClick(
    elementName: String,
    screenName: String,
    elementType: String = "button",
    enabled: Boolean = true,
    onClickLabel: String? = null,
    onClick: () -> Unit
): Modifier {
    val context = LocalContext.current
    val repository = remember { AnalyticsRepository.getInstance(context) }
    val interactionSource = remember { MutableInteractionSource() }

    return this.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        interactionSource = interactionSource,
        indication = null
    ) {
        // Record analytics event
        repository.recordElementTap(
            elementName = elementName,
            screenName = screenName,
            elementType = elementType
        )
        // Execute original click handler
        onClick()
    }
}

/**
 * Trackable Button composable that automatically records tap events
 * Drop-in replacement for standard Wear Button with analytics
 *
 * @param elementName Name of the button for analytics
 * @param screenName Name of the current screen
 * @param onClick Click handler
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 * @param colors Button colors
 * @param content Button content
 */
@Composable
fun TrackableButton(
    elementName: String,
    screenName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val repository = remember { AnalyticsRepository.getInstance(context) }

    Button(
        onClick = {
            // Record analytics event
            repository.recordElementTap(
                elementName = elementName,
                screenName = screenName,
                elementType = "button"
            )
            // Execute original click handler
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        content = content
    )
}

/**
 * Helper function to track element tap manually
 * Useful for custom interactions that don't use standard click handlers
 */
@Composable
fun rememberAnalyticsTracker(): (String, String, String) -> Unit {
    val context = LocalContext.current
    val repository = remember { AnalyticsRepository.getInstance(context) }

    return remember {
        { elementName: String, screenName: String, elementType: String ->
            repository.recordElementTap(elementName, screenName, elementType)
        }
    }
}
