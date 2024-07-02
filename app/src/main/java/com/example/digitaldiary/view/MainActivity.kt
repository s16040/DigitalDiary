package com.example.digitaldiary.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.digitaldiary.ui.theme.DigitalDiaryTheme
import com.example.digitaldiary.viewmodel.NoteViewModel
import com.example.digitaldiary.viewmodel.MainViewModelFactory
import android.app.Application
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.livedata.observeAsState
import com.example.digitaldiary.model.Note

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels {
        MainViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DigitalDiaryTheme {
                Scaffold(
                    content = { innerPadding ->
                        MainScreen(viewModel, Modifier.padding(innerPadding))
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: NoteViewModel, modifier: Modifier = Modifier) {
    var noteText by remember { mutableStateOf(TextFieldValue("")) }
    val notes by viewModel.notes.observeAsState(initial = emptyList())

    Column(
        modifier = modifier
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

@Preview(apiLevel = 34, device = "id:pixel_8")
@Composable
fun MainScreenPreview() {
    DigitalDiaryTheme {
        MainScreen(viewModel = NoteViewModel(Application()))
    }
}
