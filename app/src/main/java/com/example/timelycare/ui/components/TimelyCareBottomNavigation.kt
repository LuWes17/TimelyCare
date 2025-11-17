package com.example.timelycare.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.timelycare.data.LanguageOption
import com.example.timelycare.ui.theme.*
import com.example.timelycare.ui.theme.LocalUserSettings

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun TimelyCareBottomNavigation(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val lang = LocalUserSettings.current.language
    val items = listOf(
        BottomNavItem(
            title = if (lang == LanguageOption.FILIPINO) "Dashboard" else "Dashboard",
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            title = if (lang == LanguageOption.FILIPINO) "Gamot" else "Medications",
            icon = Icons.Default.Add
        ),
        BottomNavItem(
            title = if (lang == LanguageOption.FILIPINO) "Kalendaryo" else "Calendar",
            icon = Icons.Default.DateRange
        ),
        BottomNavItem(
            title = if (lang == LanguageOption.FILIPINO) "Kontak" else "Contacts",
            icon = Icons.Default.Person
        )
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