package com.example.androidpractice.ui.screens.filter

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController
import com.example.androidpractice.data.FilterBadgeCache
import com.example.androidpractice.di.AppModule
import com.example.androidpractice.ui.viewmodel.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController,
    viewModel: PersonViewModel,
    badgeCache: FilterBadgeCache = AppModule.filterBadgeCache
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("filter_prefs", Context.MODE_PRIVATE)

    var showMale by remember { mutableStateOf(prefs.getBoolean("showMale", true)) }
    var showFemale by remember { mutableStateOf(prefs.getBoolean("showFemale", true)) }
    var profession by remember { mutableStateOf(prefs.getString("professionFilter", "") ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фильтр") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.Companion.height(8.dp))

            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                Checkbox(checked = showMale, onCheckedChange = { showMale = it })
                Text("Показывать мужчин")
            }

            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                Checkbox(checked = showFemale, onCheckedChange = { showFemale = it })
                Text("Показывать женщин")
            }

            Spacer(modifier = Modifier.Companion.height(24.dp))

            Text("Фильтр по профессии:")
            Spacer(modifier = Modifier.Companion.height(8.dp))

            OutlinedTextField(
                value = profession,
                onValueChange = { profession = it },
                placeholder = { Text("Введите часть названия должности") },
                modifier = Modifier.Companion.fillMaxWidth()
            )

            Spacer(modifier = Modifier.Companion.height(32.dp))

            Button(
                onClick = {
                    prefs.edit {
                        putBoolean("showMale", showMale)
                        putBoolean("showFemale", showFemale)
                        putString("professionFilter", profession.trim())
                    }

                    val changed = !showMale || !showFemale || profession.trim() != ""
                    badgeCache.setChanged(changed)

                    viewModel.setGenderFilter(showMale, showFemale)
                    viewModel.setJobFilter(if (profession.isBlank()) null else profession.trim())
                    navController.popBackStack()
                },
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                Text("Сохранить и применить")
            }

            Spacer(modifier = Modifier.Companion.height(12.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.Companion.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Выйти без сохранения")
            }
        }
    }
}