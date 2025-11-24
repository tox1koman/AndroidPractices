package com.example.androidpractice.ui.screens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.androidpractice.data.local.ProfileData
import com.example.androidpractice.data.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import androidx.core.net.toUri
import com.example.androidpractice.receiver.NotificationReceiver
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("UnrememberedMutableState", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    repository: ProfileRepository = ProfileRepository(LocalContext.current)
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profileState by repository.profile.collectAsState(initial = ProfileData())

    var name by remember { mutableStateOf("") }
    var job by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf("") }
    var resumeUrl by remember { mutableStateOf("") }

    var showImageSourceDialog by remember { mutableStateOf(false) }

    var favoriteTime by remember { mutableStateOf("") }
    var favoriteTimeError by remember { mutableStateOf<String?>(null) }

    val timeRegex = Regex("""^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$""")

    LaunchedEffect(profileState) {
        name = profileState.fullName
        job = profileState.jobTitle
        avatarUri = profileState.avatarUri
        resumeUrl = profileState.resumeUri
        favoriteTime = profileState.favoritePairTime
    }

    val isFormValid by derivedStateOf {
        name.isNotBlank() && (favoriteTime.isEmpty() || favoriteTime.matches(timeRegex))
    }

    val pickFromGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val savedUri = saveImageToInternalStorage(context, it)
                avatarUri = savedUri.toString()
            }
        }
    }

    val photoFile = remember { File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg") }
    val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) avatarUri = photoUri.toString()
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePictureLauncher.launch(photoUri)
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickFromGalleryLauncher.launch("image/*")
        }
    }

    fun checkCameraPermissionAndLaunch() {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> takePictureLauncher.launch(photoUri)
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        showImageSourceDialog = false
    }

    fun checkGalleryPermissionAndLaunch(){
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        when (ContextCompat.checkSelfPermission(context, permission)) {
            PackageManager.PERMISSION_GRANTED -> pickFromGalleryLauncher.launch("image/*")
            else -> galleryPermissionLauncher.launch(permission)
        }
        showImageSourceDialog = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                repository.saveProfile(
                                    ProfileData(
                                        fullName = name,
                                        jobTitle = job,
                                        avatarUri = avatarUri,
                                        resumeUri = resumeUrl,
                                        favoritePairTime = favoriteTime.takeIf { it.matches(timeRegex) } ?: ""
                                    )
                                )
                                cancelFavoritePairAlarm(context)
                                if (favoriteTime.matches(timeRegex)) {
                                    scheduleFavoritePairNotification(context, favoriteTime, name)
                                }
                                navController.popBackStack()
                            }
                        },
                        enabled = isFormValid
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (avatarUri.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(avatarUri),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable { showImageSourceDialog = true },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable { showImageSourceDialog = true }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("Фото", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = job,
                onValueChange = { job = it },
                label = { Text("Должность") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = resumeUrl,
                onValueChange = { resumeUrl = it },
                label = { Text("Ссылка на PDF-резюме") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = favoriteTime,
                onValueChange = { newValue ->
                    favoriteTime = newValue
                    favoriteTimeError = when {
                        newValue.isEmpty() -> null
                        newValue.matches(timeRegex) -> null
                        else -> "Введите время в формате ЧЧ:ММ"
                    }
                },
                label = { Text("Время любимой пары") },
                placeholder = { Text("14:30") },
                isError = favoriteTimeError != null,
                supportingText = favoriteTimeError?.let { { Text(it) } },
                trailingIcon = {
                    IconButton(onClick = {
                        val cal = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                favoriteTime = String.format("%02d:%02d", hour, minute)
                                favoriteTimeError = null
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                        ).show()
                    }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Выбрать время")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (resumeUrl.isNotEmpty()) {
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = resumeUrl.toUri()
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Открыть резюме", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Выбрать фото") },
            text = { Text("Откуда загрузить изображение?") },
            confirmButton = {
                Column {
                    TextButton(onClick = {
                        checkCameraPermissionAndLaunch()
                    }) { Text("Сделать фото") }

                    TextButton(onClick = {
                        checkGalleryPermissionAndLaunch()
                    }) { Text("Выбрать из галереи") }
                }
            }
        )
    }
}

suspend fun saveImageToInternalStorage(context: Context, uri: Uri): Uri =
    withContext(Dispatchers.IO) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "avatar_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        Uri.fromFile(file)
    }

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("ServiceCast")
private fun scheduleFavoritePairNotification(context: Context, time: String, name: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("name", name.ifBlank { "Друг" })
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, 1001, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val (hour, minute) = time.split(":").map { it.toInt() }
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
    }

    if (!alarmManager.canScheduleExactAlarms()) {
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    } else {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    createNotificationChannel(context)
}

private fun cancelFavoritePairAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 1001, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}

private fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        "favorite_pair_channel",
        "Любимая пара",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Уведомления о начале любимой пары"
    }
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
}