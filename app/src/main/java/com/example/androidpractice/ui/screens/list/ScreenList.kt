package com.example.androidpractice.ui.screens.list

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.androidpractice.data.FilterBadgeCache
import com.example.androidpractice.data.local.AppDatabase
import com.example.androidpractice.data.local.FavoritePerson
import com.example.androidpractice.data.repository.FavoriteRepository
import com.example.androidpractice.di.AppModule
import com.example.androidpractice.ui.screens.details.DetailsActivity
import com.example.androidpractice.ui.viewmodel.PersonViewModel
import com.example.androidpractice.ui.viewmodel.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenList(
    navController: NavController,
    viewModel: PersonViewModel,
    badgeCache: FilterBadgeCache = AppModule.filterBadgeCache
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val dao = remember { AppDatabase.Companion.getInstance(context).favoriteDao() }
    val repo = remember { FavoriteRepository(dao) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val prefs = context.getSharedPreferences("filter_prefs", Context.MODE_PRIVATE)
    val changed = !prefs.getBoolean("showMale", true) ||
            !prefs.getBoolean("showFemale", true) ||
            prefs.getString("professionFilter", "") != ""

    badgeCache.setChanged(changed)
    val badgeState by badgeCache.showBadge.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    IconButton(onClick = { navController.navigate("favorites") }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Избранное")
                    }
                    IconButton(onClick = { navController.navigate("filter") }) {
                        BadgedBox(badge = {
                            if (badgeState) Badge {}
                        }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Фильтр")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.Companion.padding(paddingValues)) {
            when (uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.Companion.fillMaxSize(),
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier.Companion.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Companion.CenterHorizontally
                    ) {
                        Text(uiState.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.Companion.height(16.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text("Повторить")
                        }
                    }
                }

                is UiState.Success -> {
                    LazyColumn(modifier = Modifier.Companion.fillMaxSize()) {
                        items(uiState.persons) { person ->

                            val favPerson = FavoritePerson(
                                id = person.id,
                                firstName = person.firstName,
                                lastName = person.lastName,
                                gender = person.gender,
                                jobTitle = person.company?.title,
                                imageUrl = person.image,
                                bio = person.bio
                            )

                            Card(
                                modifier = Modifier.Companion
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .combinedClickable(
                                        onClick = {
                                            val intent =
                                                Intent(context, DetailsActivity::class.java).apply {
                                                    putExtra("PERSON_ID", person.id)
                                                    putExtra("PERSON_FIRSTNAME", person.firstName)
                                                    putExtra("PERSON_LASTNAME", person.lastName)
                                                    putExtra(
                                                        "PERSON_BIO",
                                                        person.bio ?: "Нет биографии"
                                                    )
                                                    putExtra(
                                                        "PERSON_GENDER",
                                                        if (person.gender == "male") "Мужской" else "Женский"
                                                    )
                                                    putExtra(
                                                        "PERSON_JOBTITLE",
                                                        person.company?.title
                                                    )
                                                    putExtra("PERSON_IMAGE_URL", person.image)
                                                }
                                            context.startActivity(intent)
                                        },
                                        onLongClick = {
                                            scope.launch {
                                                val isFavorite = repo.isFavorite(favPerson.id)
                                                if (isFavorite) {
                                                    repo.removeFavorite(favPerson)
                                                    snackbarHostState.showSnackbar("Удалено из избранного")
                                                } else {
                                                    repo.addFavorite(favPerson)
                                                    snackbarHostState.showSnackbar("Добавлено в избранное")
                                                }
                                            }
                                        }
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Companion.White,
                                    contentColor = Color.Companion.DarkGray
                                ),
                                shape = RectangleShape,
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.Companion
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .height(80.dp),
                                    verticalAlignment = Alignment.Companion.CenterVertically
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(person.image),
                                        contentDescription = "Portrait",
                                        modifier = Modifier.Companion
                                            .size(60.dp)
                                            .padding(end = 12.dp)
                                    )
                                    Column(modifier = Modifier.Companion.weight(1f)) {
                                        Text(
                                            text = "${person.firstName} ${person.lastName}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = person.company?.title ?: "Нет должности",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}