package com.example.explorelocal.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Umkm : BottomNavItem(
        route = "umkm_list",
        title = "UMKM",
        icon = Icons.Default.Store
    )

    object Promo : BottomNavItem(
        route = "promo",
        title = "Promo",
        icon = Icons.Default.Percent
    )

    object Location : BottomNavItem(
        route = "location",
        title = "Lokasi",
        icon = Icons.Default.LocationOn
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}
