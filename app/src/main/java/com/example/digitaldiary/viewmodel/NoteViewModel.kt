package com.example.digitaldiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiary.model.Note
import com.example.digitaldiary.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> get() = _notes

    fun loadNotes(userId: String) {
        viewModelScope.launch {
            repository.getNotesByUser(userId).collect { snapshot ->
                _notes.value = snapshot.toObjects(Note::class.java)
            }
        }
    }

    fun addNote(
        title: String,
        content: String,
        userId: String,
        imageUrl: String? = null,
        audioUrl: String? = null
    ) {
        viewModelScope.launch {
            val location = "Some Location" // Implement location fetching logic
            val city = "Some City" // Implement city fetching logic
            val timestamp = System.currentTimeMillis()
            val sdf = SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault())
            val date = sdf.format(Date(timestamp))
            val noteContent = "$content\n#$city #$date"

            val note = Note(
                title = title,
                content = noteContent,
                userId = userId,
                imageUrl = imageUrl,
                audioUrl = audioUrl,
                location = location,
                timestamp = timestamp,
                city = city
            )
            repository.addNote(note)
        }
    }

    fun getNoteById(noteId: String): StateFlow<Note?> {
        val noteFlow = MutableStateFlow<Note?>(null)
        viewModelScope.launch {
            repository.getNoteById(noteId).collect { note ->
                noteFlow.value = note
            }
        }
        return noteFlow
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }
}
