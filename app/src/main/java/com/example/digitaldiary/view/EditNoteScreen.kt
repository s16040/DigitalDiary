package com.example.digitaldiary.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
//import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Environment
import android.util.Log
//import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.digitaldiary.R
import com.example.digitaldiary.utils.MediaUtils
import com.example.digitaldiary.viewmodel.NoteViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.IOException
import java.util.*
import java.text.SimpleDateFormat

suspend fun updateLocationAndCity(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    callback: (Location?, String?) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    withContext(Dispatchers.IO) {
        try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                var cityName: String? = null
                var retryCount = 0
                val maxRetries = 3
                while (retryCount < maxRetries) {
                    try {
                        val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            cityName = addresses[0].locality
                            break
                        }
                    } catch (e: IOException) {
                        retryCount++
                    }
                }
                withContext(Dispatchers.Main) {
                    callback(it, cityName)
                }
            } ?: withContext(Dispatchers.Main) {
                callback(null, null)
            }
        } catch (e: Exception) {
            Log.e("EditNoteScreen", "Błąd przy aktualizacji lokalizacji", e)
            withContext(Dispatchers.Main) {
                callback(null, null)
            }
        }
    }
}
private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}
//val imagePicker = rememberLauncherForActivityResult(
//    contract = ActivityResultContracts.TakePicture()
//) { success ->
//    if (success) {
//        photoUri?.let { uri ->
//            viewModel.updateImage(uri)
//        }
//    }
//}
//
//@Composable
//fun ImagePreview(imageUrl: String?) {
//    if (imageUrl != null) {
//        AsyncImage(
//            model = imageUrl,
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//        )
//    }
//}
@Composable
fun EditNoteScreen(navController: NavHostController, viewModel: NoteViewModel, noteId: String) {
    val context = LocalContext.current

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val noteState by viewModel.noteState.collectAsState()
    val mediaUtils = remember { MediaUtils(context) }
    var isRecording by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
            if (success) {
                photoUri?.let { uri ->
                    viewModel.updatePhotoUri(uri.toString())
                }
            }
        }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()

    var noteTitle by remember { mutableStateOf(TextFieldValue("")) }
    var noteContent by remember { mutableStateOf(TextFieldValue("")) }
    var city by remember { mutableStateOf("") }
    var timestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var initialCity by remember { mutableStateOf("") }
    var initialTimestamp by remember { mutableLongStateOf(0L) }

    LaunchedEffect(noteId) {
        viewModel.getNoteById(noteId)
    }

    LaunchedEffect(noteState) {
        noteState?.let { note ->
            noteTitle = TextFieldValue(note.title)
            noteContent = TextFieldValue(note.content)
            city = note.city
            timestamp = note.timestamp
            initialCity = note.city
            initialTimestamp = note.timestamp
        }
    }

//    val locationPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestMultiplePermissions(),
//        onResult = { permissions ->
//            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
//                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
//            ) {
//                scope.launch {
//                    updateLocationAndCity(fusedLocationClient, context) { loc, cityName ->
//                        loc?.let {
//                            city = cityName ?: ""
//                            timestamp = System.currentTimeMillis()
//                        }
//                    }
//                }
//            }
//        }
//    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {

        TextField(
            value = noteTitle,
            onValueChange = { noteTitle = it },
            label = {Text(stringResource(R.string.note_title)) },
            modifier = Modifier.fillMaxWidth()
            )

    Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = noteContent,
            onValueChange = { noteContent = it },
            label = {Text(stringResource(R.string.note_content)) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            )
        var isRecording by remember { mutableStateOf(false) }
        val mediaUtils = remember { MediaUtils(context) }


    Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.updateNote(
                    noteState!!.copy(
                        title = noteTitle.text,
                        content = noteContent.text,
                        city = city,
                        timestamp = System.currentTimeMillis()
                        )
                    )
                navController.popBackStack()
                },
            modifier = Modifier
                .fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_note))
            }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
        Button(
                onClick = {
                    val tempUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        createImageFile(context)
                    )
                    photoUri = tempUri
                    launcher.launch(tempUri)
                },
            modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.add_photo))
            }

        Button(
                onClick = {
                    if (isRecording) {
                        mediaUtils.stopRecording()?.let { filePath ->
                            viewModel.updateAudioPath(filePath)
                        }
                    } else {
                        mediaUtils.startRecording()
                    }
                    isRecording = !isRecording
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    if (isRecording)
                        stringResource(R.string.stop_recording)
                    else
                        stringResource(R.string.start_recording)
                )
            }
        }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
        Button(
            onClick = {
                    viewModel.deleteNote(noteId)
                    navController.popBackStack()
                },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(stringResource(R.string.delete))
            }
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
            ) {
                Text(stringResource(R.string.back))
            }

        }



        LaunchedEffect(Unit) {
            scope.launch {
                updateLocationAndCity(fusedLocationClient, context) { loc, cityName ->
                    loc?.let {
                        city = cityName ?: ""
                        timestamp = System.currentTimeMillis()
                    }
                }
            }
        }
    }
}
