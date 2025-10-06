package com.example.androidpractice

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph(navController: NavHostController){
    NavHost(navController, startDestination = Routes.Home.route){
        composable (BottomNavigationItems.ScreenHome.route) {
            ScreenHome()
        }
        composable (BottomNavigationItems.ScreenList.route) {
            ScreenList()
        }
    }
}