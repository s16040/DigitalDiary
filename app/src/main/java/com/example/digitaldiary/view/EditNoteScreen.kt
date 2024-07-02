package com.example.digitaldiary.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitaldiary.model.Note
import com.example.digitaldiary.viewmodel.NoteViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun EditNoteScreen(navController: NavController, viewModel: NoteViewModel, noteId: String) {
    val coroutineScope = rememberCoroutineScope()
    val noteState = remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(noteId) {
        coroutineScope.launch {
            viewModel.getNoteById(noteId).collectLatest { note ->
                noteState.value = note
            }
        }
    }

    val note = noteState.value

    note?.let {
        var noteTitle by remember { mutableStateOf(it.title) }
        var noteContent by remember { mutableStateOf(it.content) }

        Column(modifier = Modifier.padding(16.dp)) {
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
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.updateNote(it.copy(title = noteTitle, content = noteContent))
                navController.popBackStack()
            }) {
                Text("Zatwierdź Notatkę")
            }
        }
    }
}
