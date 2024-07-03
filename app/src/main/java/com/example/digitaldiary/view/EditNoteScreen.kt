package com.example.digitaldiary.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.digitaldiary.viewmodel.NoteViewModel

@Composable
fun EditNoteScreen(navController: NavHostController, viewModel: NoteViewModel, noteId: String) {
    val noteState by viewModel.getNoteById(noteId).collectAsState(initial = null)

    var noteTitle by remember { mutableStateOf(TextFieldValue("")) }
    var noteContent by remember { mutableStateOf(TextFieldValue("")) }
    var city by remember { mutableStateOf("") }
    var timestamp by remember { mutableLongStateOf(0L) }

    LaunchedEffect(noteState) {
        noteState?.let { note ->
            noteTitle = TextFieldValue(note.title)
            noteContent = TextFieldValue(note.content)
            city = note.city ?: ""
            timestamp = note.timestamp
        }
    }

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
                Text("Zapisz notatkę")
            }
            Button(
                onClick = { /* Add image functionality */ },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Dodaj Zdjęcie")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Add audio recording functionality */ },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Dodaj Nagranie Głosowe")
            }
            Button(
                onClick = {
                    viewModel.deleteNoteById(noteId)
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
    }
}
