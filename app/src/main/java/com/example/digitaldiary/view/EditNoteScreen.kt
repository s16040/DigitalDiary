package com.example.digitaldiary.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.digitaldiary.viewmodel.NoteViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.*

fun updateLocationAndCity(
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

    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        location?.let {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val cityName = addresses[0].locality ?: ""
                callback(it, cityName)
            } else {
                callback(null, null)
            }
        } ?: callback(null, null)
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
            city = note.city ?: ""
            timestamp = note.timestamp
            initialCity = note.city ?: ""
            initialTimestamp = note.timestamp
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                updateLocationAndCity(fusedLocationClient, context) { loc, cityName ->
                    loc?.let {
                        city = cityName ?: ""
                        timestamp = System.currentTimeMillis()
                    }
                }
            }
        }
    )

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
        Button(onClick = {
            noteState?.let { note ->
                val initialInfo = if (initialCity.isNotEmpty() && initialTimestamp != 0L) {
                    "Notatka poprzednim razem utworzona w miejscowości $initialCity. Moment utworzenia to ${Date(initialTimestamp)}\n\n"
                } else {
                    ""
                }
                viewModel.updateNote(
                    note.copy(
                        title = noteTitle.text,
                        content = initialInfo + noteContent.text,
                        city = city,
                        timestamp = timestamp
                    )
                )
                navController.popBackStack()
            }
        }) {
            Text("Zapisz notatkę")
        }
    }

    // Uaktualnienie lokalizacji i miasta przy uruchomieniu ekranu
    LaunchedEffect(Unit) {
        updateLocationAndCity(fusedLocationClient, context) { loc, cityName ->
            loc?.let {
                city = cityName ?: ""
                timestamp = System.currentTimeMillis()
            }
        }
    }
}
