package com.example.digitaldiary.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.digitaldiary.R
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

    private fun getLastKnownLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            callback(location)
        }
    }

    @Composable
    fun AppNavHost(
        navController: NavHostController,
        viewModel: NoteViewModel,
        userId: String,
        fusedLocationClient: FusedLocationProviderClient,
        onLogout: () -> Unit
    ) {
        NavHost(navController = navController, startDestination = "mainScreen") {
            composable("mainScreen") {
                MainScreen(viewModel, userId, navController, onLogout, fusedLocationClient)
            }
            composable("previousNotes") {
                PreviousNotesScreen(navController, viewModel, userId)
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
        onLogout: () -> Unit,
        fusedLocationClient: FusedLocationProviderClient
    ) {
        var noteTitle by remember { mutableStateOf(TextFieldValue("")) }
        var noteContent by remember { mutableStateOf(TextFieldValue("")) }
        var location by remember { mutableStateOf<Location?>(null) }
        var city by remember { mutableStateOf("") }
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            getLastKnownLocation { loc ->
                location = loc
                loc?.let {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        city = addresses[0].locality ?: ""
                    }
                }
            }
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
                label = { Text(stringResource(id = R.string.title_note)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                label = { Text(stringResource(id = R.string.content_note)) },
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
                    getLastKnownLocation { loc ->
                        location = loc
                        loc?.let {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                city = addresses[0].locality ?: ""
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.add_note))
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
                    Text(stringResource(id = R.string.add_image))
                }
                Button(
                    onClick = { /* Add audio recording functionality */ },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(stringResource(id = R.string.add_audio))
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
                    Text(stringResource(id = R.string.map_notes))
                }
                Button(
                    onClick = {
                        navController.navigate("previousNotes")
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(stringResource(id = R.string.previous_notes))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.logout))
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
                label = { Text(stringResource(id = R.string.password)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                auth.signInWithEmailAndPassword(email.text, password.text)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onLoginSuccess()
                        } else {
                            // Obsługa błędu logowania?
                        }
                    }
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.login))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                auth.createUserWithEmailAndPassword(email.text, password.text)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onLoginSuccess()
                        } else {
                            // Obsługa błędu rejestracji?
                        }
                    }
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.register))
            }
        }
    }
}
