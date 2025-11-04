package com.example.androidpractice.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationItems(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    object ScreenHome: BottomNavigationItems(
        route = "home",
        title = "Дом",
        icon = Icons.Filled.Home
    )

    object ScreenList: BottomNavigationItems(
        route = "list",
        title = "Список",
        icon = Icons.AutoMirrored.Filled.List
    )

    object ScreenProfile : BottomNavigationItems(
        "profile",
        "Профиль",
        Icons.Default.Person)

}