package com.example.androidpractice.ui.screens.favorites

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.androidpractice.data.local.FavoriteDao
import com.example.androidpractice.data.local.FavoritePerson
import com.example.androidpractice.ui.viewmodel.FavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(dao: FavoriteDao, onBack: () -> Unit = {}, navController: NavController) {
    val favoriteViewModel: FavoriteViewModel = viewModel()
    favoriteViewModel.loadFavorites(dao)

    val favoriteList = favoriteViewModel.favorites.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.Companion.padding(paddingValues)) {
            if (favoriteList.value.isEmpty()) {
                Box(
                    modifier = Modifier.Companion.fillMaxSize(),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Text("Нет избранного")
                }
            } else {
                LazyColumn(modifier = Modifier.Companion.fillMaxSize()) {
                    items(favoriteList.value) { person: FavoritePerson ->
                        Row(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("details/${person.id}")
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.Companion.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(person.imageUrl),
                                contentDescription = "Portrait",
                                modifier = Modifier.Companion.size(60.dp)
                            )
                            Spacer(modifier = Modifier.Companion.width(12.dp))
                            Column {
                                Text(text = "${person.firstName} ${person.lastName}")
                                Text(text = person.jobTitle ?: "Нет должности")
                            }
                        }
                    }
                }
            }
        }
    }
}