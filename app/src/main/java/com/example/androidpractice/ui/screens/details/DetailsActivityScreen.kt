package com.example.androidpractice.ui.screens.details

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.androidpractice.di.AppModule
import com.example.androidpractice.domain.model.Person
import com.example.androidpractice.ui.viewmodel.PersonViewModel
import com.example.androidpractice.ui.viewmodel.UiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsActivityScreen(
    personId: Int,
    onBackClick: () -> Unit = {},
    viewModel: PersonViewModel
) {
    LaunchedEffect(personId) {
        viewModel.loadPersonById(personId)
    }

    val personState by viewModel.selectedPerson.collectAsState()

    when (personState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: ${(personState as UiState.Error).message}", color = MaterialTheme.colorScheme.error)
            }
        }
        is UiState.Success -> {
            val person = (personState as UiState.Success).persons.first()
            DetailsContent(person = person, onBackClick = onBackClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsContent(person: Person, onBackClick: () -> Unit) {
    val imageScale = remember { Animatable(0f) }
    val contentOpacity = remember { Animatable(0f) }

    val rainbowColors = listOf(Color.Cyan, Color(0xFF0066FF), Color(0xFF800080))

    LaunchedEffect(Unit) {
        imageScale.animateTo(1f, tween(800))
        delay(200)
        contentOpacity.animateTo(1f, tween(600))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    FloatingActionButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(8.dp).size(40.dp),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(16.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(person.image),
                        contentDescription = null,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .shadow(4.dp, CircleShape)
                            .scale(imageScale.value)
                    )

                    Text(
                        text = "${person.firstName} ${person.lastName}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        ),
                        modifier = Modifier.alpha(contentOpacity.value)
                    )

                    Text(
                        text = person.company.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .alpha(contentOpacity.value)
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )

                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Любимая цитата: ") }
                            withStyle(SpanStyle(brush = Brush.linearGradient(rainbowColors), fontStyle = FontStyle.Italic)) {
                                append("«${person.bio}»")
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, lineHeight = 24.sp),
                        modifier = Modifier.alpha(contentOpacity.value)
                    )

                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Пол: ") }
                            append(person.gender)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.alpha(contentOpacity.value)
                    )
                }
            }
        }
    }
}