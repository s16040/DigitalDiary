package com.example.digitaldiary.view

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.digitaldiary.ui.theme.DigitalDiaryTheme
import com.example.digitaldiary.viewmodel.NoteViewModel
import com.example.digitaldiary.viewmodel.NoteViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel: NoteViewModel by viewModels { NoteViewModelFactory() }
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            DigitalDiaryTheme {
                val user = auth.currentUser
                val navController = rememberNavController()
                if (user != null) {
                    AppNavHost(navController, viewModel, user.uid, fusedLocationClient) {
                        lifecycleScope.launch {
                            auth.signOut()
                            recreate()
                        }
                    }
                } else {
                    LoginScreen(onLoginSuccess = {
                        recreate()
                    })
                }
            }
        }
    }

    fun getLastKnownLocation(context: Context, callback: (Location?, String?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = location?.let { geocoder.getFromLocation(it.latitude, it.longitude, 1) }
            val cityName = addresses?.firstOrNull()?.locality
            callback(location, cityName)
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, viewModel: NoteViewModel, userId: String, fusedLocationClient: FusedLocationProviderClient, onLogout: () -> Unit) {
    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            MainScreen(viewModel, userId, navController, fusedLocationClient, onLogout)
        }
        composable("previousNotes") {
            PreviousNotesScreen(navController, viewModel, userId, onLogout)
        }
        composable("editNote/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            EditNoteScreen(navController, viewModel, noteId)
        }
    }
}

@Composable
fun MainScreen(
    viewModel: NoteViewModel,
    userId: String,
    navController: NavHostController,
    fusedLocationClient: FusedLocationProviderClient,
    onLogout: () -> Unit
) {
    var noteTitle by remember { mutableStateOf(TextFieldValue("")) }
    var noteContent by remember { mutableStateOf(TextFieldValue("")) }
    var location by remember { mutableStateOf<Location?>(null) }
    var city by remember { mutableStateOf("") }
    var timestamp by remember { mutableStateOf(0L) }

    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                (context as MainActivity).getLastKnownLocation(context) { loc, cityName ->
                    loc?.let {
                        location = it
                        city = cityName ?: ""
                        timestamp = System.currentTimeMillis()
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
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
        Button(
            onClick = {
                viewModel.addNote(
                    title = noteTitle.text,
                    content = noteContent.text,
                    userId = userId,
                    city = city
                )
                noteTitle = TextFieldValue("")
                noteContent = TextFieldValue("")
                (context as MainActivity).getLastKnownLocation(context) { loc, cityName ->
                    loc?.let {
                        location = it
                        city = cityName ?: ""
                        timestamp = System.currentTimeMillis()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zatwierdź Notatkę")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Add image functionality */ },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Dodaj Zdjęcie")
            }
            Button(
                onClick = { /* Add audio recording functionality */ },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Dodaj Nagranie Głosowe")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Navigate to Map Screen */ },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Mapa Notatek")
            }
            Button(
                onClick = {
                    navController.navigate("previousNotes")
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Poprzednie Notatki")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Wyloguj")
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            auth.signInWithEmailAndPassword(email.text, password.text)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onLoginSuccess()
                    } else {
                        // Obsługa błędu logowania
                    }
                }
        }) {
            Text("Zaloguj się")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            auth.createUserWithEmailAndPassword(email.text, password.text)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onLoginSuccess()
                    } else {
                        // Obsługa błędu rejestracji
                    }
                }
        }) {
            Text("Zarejestruj się")
        }
    }
}
