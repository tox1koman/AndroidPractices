package com.example.androidpractice.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidpractice.navigation.BottomNavigationItems
import com.example.androidpractice.navigation.Routes
import com.example.androidpractice.ui.screens.home.ScreenHome
import com.example.androidpractice.ui.screens.list.ScreenList
import com.example.androidpractice.data.local.AppDatabase
import com.example.androidpractice.di.AppModule
import com.example.androidpractice.ui.screens.favorites.FavoriteScreen
import com.example.androidpractice.ui.screens.filter.FilterScreen
import com.example.androidpractice.ui.viewmodel.PersonViewModel

@Composable
fun NavigationGraph(navController: NavHostController){
    val context = LocalContext.current
    val db = AppDatabase.Companion.getInstance(context)
    val favoriteDao = db.favoriteDao()
    val personViewModel: PersonViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PersonViewModel(AppModule.getPersonsUseCase, context) as T
            }
        }
    )
    NavHost(navController, startDestination = Routes.Home.route) {
        composable(BottomNavigationItems.ScreenHome.route) { ScreenHome() }
        composable(BottomNavigationItems.ScreenList.route) {
            ScreenList(
                navController,
                personViewModel
            )
        }
        composable("filter") { FilterScreen(navController, personViewModel) }
        composable("favorites") {
            FavoriteScreen(
                favoriteDao,
                onBack = { navController.popBackStack() })
        }
    }
}