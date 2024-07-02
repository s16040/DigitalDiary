package com.example.digitaldiary.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.digitaldiary.viewmodel.NoteViewModel

@Composable
fun EditNoteScreen(navController: NavHostController, viewModel: NoteViewModel, noteId: String) {
    val noteState by viewModel.getNoteById(noteId).collectAsState(initial = null)

    var noteTitle by remember { mutableStateOf(TextFieldValue("")) }
    var noteContent by remember { mutableStateOf(TextFieldValue("")) }

    noteState?.let { note ->
        noteTitle = TextFieldValue(note.title)
        noteContent = TextFieldValue(note.content)
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
        Button(onClick = {
            noteState?.let { note ->
                viewModel.updateNote(
                    note.copy(
                        title = noteTitle.text,
                        content = noteContent.text
                    )
                )
                navController.popBackStack()
            }
        }) {
            Text("Zapisz notatkę")
        }
    }
}
