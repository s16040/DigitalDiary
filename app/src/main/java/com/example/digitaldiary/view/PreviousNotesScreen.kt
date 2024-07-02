package com.example.digitaldiary.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.digitaldiary.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PreviousNotesScreen(navController: NavController, viewModel: NoteViewModel, userId: String) {
    val notes by viewModel.notes.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn {
            items(notes) { note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("editNote/${note.id}")
                        }
                        .padding(8.dp)
                ) {
                    Column {
                        Text(text = note.title, style = MaterialTheme.typography.bodyLarge)
                        Text(text = note.city, style = MaterialTheme.typography.bodySmall)
                        Text(text = SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault()).format(Date(note.timestamp)), style = MaterialTheme.typography.bodySmall)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
