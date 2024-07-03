package com.example.digitaldiary.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.digitaldiary.viewmodel.NoteViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.*

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
            withContext(Dispatchers.Main) {
                callback(null, null)
            }
        }
    }
}

@Composable
fun EditNoteScreen(navController: NavHostController, viewModel: NoteViewModel, noteId: String) {
    val noteState by viewModel.noteState.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()

    var noteTitle by remember { mutableStateOf(TextFieldValue("")) }
    var noteContent by remember { mutableStateOf(TextFieldValue("")) }
    var city by remember { mutableStateOf("") }
    var timestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    var initialCity by remember { mutableStateOf("") }
    var initialTimestamp by remember { mutableStateOf(0L) }

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
            label = { Text("Tytuł notatki") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = noteContent,
            onValueChange = { noteContent = it },
            label = { Text("Treść notatki") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Wiersz1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

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
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Zapisz Notatkę")
            }

            Button(
                onClick = { /* dodanie zdjęcia */ },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Dodaj Zdjęcie")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Wiersz2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = { /* magranie dzwięku */ },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Dodaj Nagranie")
            }

            Button(
                onClick = {
                    viewModel.deleteNote(noteId)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Usuń")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Wróć")
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
