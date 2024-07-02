package com.example.digitaldiary.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.digitaldiary.model.Note
import com.example.digitaldiary.viewmodel.NoteViewModel


@Composable
fun PreviousNotesScreen(navController: NavHostController, viewModel: NoteViewModel, userId: String) {
    val notes by viewModel.notes.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.loadNotes(userId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(notes) { note ->
            NoteItem(note = note, onClick = {
                navController.navigate("editNote/${note.id}")
            })
            Divider()
        }
    }
}

@Composable
fun NoteItem(note: Note, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(text = note.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = note.city, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = note.timestamp.toString(), style = MaterialTheme.typography.bodySmall)
    }
}
