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

import androidx.activity.ComponentActivity

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.navArgument

import com.example.digitaldiary.repository.NoteRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = NoteRepository() // Zakładając, że masz odpowiednią implementację
        val viewModel: NoteViewModel by viewModels { NoteViewModelFactory(repository) }

        setContent {
            DigitalDiaryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, viewModel: NoteViewModel) {
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = "mainScreen"
    ) {
        composable("mainScreen") {
            MainScreen(navController, viewModel)
        }
        composable(
            "editNote/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            EditNoteScreen(navController, viewModel, noteId)
        }
        composable(
            "previousNotesScreen/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            PreviousNotesScreen(navController, viewModel, userId)
        }
    }
}


@Composable
fun MainScreen(navController: NavHostController, viewModel: NoteViewModel, userId: String, onLogout: () -> Unit) {
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
