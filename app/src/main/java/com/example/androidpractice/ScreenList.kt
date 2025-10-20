// ui/ScreenList.kt
package com.example.androidpractice

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun ScreenList(
    viewModel: PersonViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PersonViewModel(AppModule.getPersonsUseCase) as T
            }
        }
    )
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    when (uiState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.retry() }) {
                    Text("Повторить")
                }
            }
        }
        is UiState.Success -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.persons) { person ->
                        Button(
                            onClick = {
                                val intent = Intent(context, DetailsActivity::class.java).apply {
                                    putExtra("PERSON_FIRSTNAME", person.firstName)
                                    putExtra("PERSON_LASTNAME", person.lastName)
                                    putExtra("PERSON_BIO", person.bio ?: "Нет биографии")
                                    putExtra("PERSON_GENDER",if (person.gender == "male") "Мужской" else "Женский")
                                    putExtra("PERSON_JOBTITLE", person.company?.title)
                                    putExtra("PERSON_IMAGE_URL", person.image)
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.DarkGray
                            ),
                             shape = RectangleShape
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .height(80.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = person.image,
                                        placeholder = androidx.compose.ui.res.painterResource(
                                            id = android.R.drawable.ic_menu_gallery
                                        )
                                    ),
                                    contentDescription = "Portrait",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(end = 12.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${person.firstName} ${person.lastName}", style = MaterialTheme.typography.titleMedium)
                                    Text(text = person.company?.title ?: "Нет должности", style = MaterialTheme.typography.bodySmall)
                                }
                        }
                    }
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }
        }
    }
}