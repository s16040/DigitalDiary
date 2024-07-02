package com.example.digitaldiary.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.digitaldiary.ui.theme.DigitalDiaryTheme
import com.example.digitaldiary.viewmodel.NoteViewModel
import com.example.digitaldiary.viewmodel.MainViewModelFactory
//import android.app.Application
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.livedata.observeAsState
import com.example.digitaldiary.model.Note
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await





class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels {
        MainViewModelFactory(application)
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        setContent {
            DigitalDiaryTheme {
                var user by remember { mutableStateOf(auth.currentUser) }
                if (user != null) {
                    MainScreen(viewModel, onLogout = {
                        auth.signOut()
                        user = null
                    })
                } else {
                    LoginScreen(
                        onLogin = { email, password ->
                            lifecycleScope.launch {
                                try {
                                    auth.signInWithEmailAndPassword(email, password).await()
                                    user = auth.currentUser
                                } catch (e: Exception) {
                                    // handle error
                                }
                            }
                        },
                        onRegister = { email, password ->
                            lifecycleScope.launch {
                                try {
                                    auth.createUserWithEmailAndPassword(email, password).await()
                                    user = auth.currentUser
                                } catch (e: Exception) {
                                    // handle error
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onRegister: (String, String) -> Unit) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }

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
        Button(onClick = { onLogin(email.text, password.text) }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onRegister(email.text, password.text) }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }
    }
}

@Composable
fun MainScreen(viewModel: NoteViewModel, onLogout: () -> Unit) {
    var noteText by remember { mutableStateOf(TextFieldValue("")) }
    val notes by viewModel.notes.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Header()
        TextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Wprowadź notatkę") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.captureLocation() }, modifier = Modifier.fillMaxWidth()) {
            Text("Pobierz lokalizację")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.addImage() }, modifier = Modifier.fillMaxWidth()) {
            Text("Dodaj zdjęcie")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.recordAudio() }, modifier = Modifier.fillMaxWidth()) {
            Text("Nagraj dźwięk")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.submitNote(noteText.text) }, modifier = Modifier.fillMaxWidth()) {
            Text("Zatwierdź notatkę")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Wyloguj")
        }
        Spacer(modifier = Modifier.height(16.dp))
        NoteList(notes)
    }
}

@Composable
fun NoteList(notes: List<Note>) {
    LazyColumn {
        items(notes) { note ->
            Text(text = note.title, style = MaterialTheme.typography.bodyLarge)
            Text(text = note.content, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Digital Diary",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
