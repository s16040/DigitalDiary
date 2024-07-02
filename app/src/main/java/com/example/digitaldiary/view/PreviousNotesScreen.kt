package com.example.digitaldiary.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.digitaldiary.model.Note
import com.example.digitaldiary.viewmodel.NoteViewModel

@Composable
fun PreviousNotesScreen(navController: NavController, viewModel: NoteViewModel, userId: String) {
    val notes by viewModel.notes.observeAsState(emptyList())

    LaunchedEffect(userId) {
        viewModel.loadNotes(userId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Poprzednie Notatki", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(notes) { note ->
                NoteItem(note, onClick = {
                    navController.navigate("edit_note/${note.id}")
                })
                Divider()
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(note.title, style = MaterialTheme.typography.titleLarge)
            Text(note.city, style = MaterialTheme.typography.bodyMedium)
            Text(note.timestamp.toString(), style = MaterialTheme.typography.bodySmall)
        }
    }
}
