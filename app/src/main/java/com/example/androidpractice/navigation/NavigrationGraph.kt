package com.example.androidpractice.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidpractice.navigation.BottomNavigationItems
import com.example.androidpractice.navigation.Routes
import com.example.androidpractice.ui.screens.home.ScreenHome
import com.example.androidpractice.ui.screens.list.ScreenList
import com.example.androidpractice.data.local.AppDatabase
import com.example.androidpractice.data.repository.ProfileRepository
import com.example.androidpractice.di.AppModule
import com.example.androidpractice.domain.model.Company
import com.example.androidpractice.domain.model.Person
import com.example.androidpractice.ui.screens.details.DetailsActivityScreen
import com.example.androidpractice.ui.screens.favorites.FavoriteScreen
import com.example.androidpractice.ui.screens.filter.FilterScreen
import com.example.androidpractice.ui.screens.profile.EditProfileScreen
import com.example.androidpractice.ui.screens.profile.ProfileScreen
import com.example.androidpractice.ui.viewmodel.PersonViewModel
import com.example.androidpractice.ui.viewmodel.ProfileViewModel
import com.example.androidpractice.ui.viewmodel.UiState

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
        composable ("details/{personId}",
            arguments = listOf(navArgument("personId") { type = NavType.IntType }))
        {backStackEntry ->
            val personId = backStackEntry.arguments?.getInt("personId") ?: return@composable
            DetailsActivityScreen(
                personId = personId,
                onBackClick = { navController.popBackStack() },
                viewModel = personViewModel
            )
        }
        composable("filter") { FilterScreen(navController, personViewModel) }
        composable("favorites") {
            FavoriteScreen(
                favoriteDao,
                onBack = { navController.popBackStack() },
                navController)
        }
        composable("profile") {
            val repo = ProfileRepository(context)
            val vm = remember { ProfileViewModel(repo) }
            ProfileScreen(navController, vm)
        }

        composable("edit_profile") {
            val repo = ProfileRepository(context)
            EditProfileScreen(navController, repo)
        }

    }
}