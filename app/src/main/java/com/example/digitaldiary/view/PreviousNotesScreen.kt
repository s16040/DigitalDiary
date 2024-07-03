package com.example.digitaldiary.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
        items(notes.sortedByDescending { it.timestamp }) { note ->
            NoteItem(note.title, note.city, note.timestamp, onClick = {
                navController.navigate("editNote/${note.id}")
            })
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun NoteItem(title: String, city: String?, timestamp: Long, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = city ?: "", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = formatDate(timestamp), style = MaterialTheme.typography.bodySmall)
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
    val netDate = java.util.Date(timestamp)
    return sdf.format(netDate)
}
