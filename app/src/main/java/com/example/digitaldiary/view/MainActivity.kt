package com.example.digitaldiary.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
//import androidx.lifecycle.lifecycleScope
import com.example.digitaldiary.viewmodel.NoteViewModel
import com.example.digitaldiary.viewmodel.NoteViewModelFactory
import com.example.digitaldiary.ui.theme.DigitalDiaryTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
//import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val viewModel: NoteViewModel by viewModels { NoteViewModelFactory() }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        setContent {
            DigitalDiaryTheme {
                val user = auth.currentUser
                val navController = rememberNavController()
                if (user != null) {
                    AppNavHost(navController, viewModel, user.uid) {
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
}

@Composable
fun AppNavHost(navController: NavHostController, viewModel: NoteViewModel, userId: String, onLogout: () -> Unit) {
    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            MainScreen(viewModel, userId, navController, onLogout)
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
fun MainScreen(viewModel: NoteViewModel, userId: String, navController: NavHostController, onLogout: () -> Unit) {
    var noteTitle by remember { mutableStateOf(TextFieldValue("")) }
    var noteContent by remember { mutableStateOf(TextFieldValue("")) }

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
                    userId = userId
                )
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
