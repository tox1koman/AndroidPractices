package com.example.androidpractice.ui.screens.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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

    LaunchedEffect(profileState) {
        name = profileState.fullName
        job = profileState.jobTitle
        avatarUri = profileState.avatarUri
        resumeUrl = profileState.resumeUri
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
                    IconButton(onClick = {
                        scope.launch {
                            repository.saveProfile(
                                ProfileData(
                                    fullName = name,
                                    jobTitle = job,
                                    avatarUri = avatarUri,
                                    resumeUri = resumeUrl
                                )
                            )
                            navController.popBackStack()
                        }
                    }) {
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
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Выбрать фото") },
            text = { Text("Откуда загрузить изображение?") },
            confirmButton = {
                Column {
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        checkCameraPermissionAndLaunch()
                    }) { Text("Сделать фото") }

                    TextButton(onClick = {
                        showImageSourceDialog = false
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
