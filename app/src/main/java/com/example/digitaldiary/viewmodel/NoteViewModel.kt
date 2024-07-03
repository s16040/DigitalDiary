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
    val notes: StateFlow<List<Note>> = _notes

    private val _noteState = MutableStateFlow<Note?>(null)
    val noteState: StateFlow<Note?> = _noteState

    fun loadNotes(userId: String) {
        viewModelScope.launch {
            _notes.value = repository.getAllNotes(userId)
        }
    }

    fun addNote(
        title: String,
        content: String,
        userId: String,
        imageUrl: String? = null,
        audioUrl: String? = null,
        location: String? = null,
        city: String = ""
    ) {
        viewModelScope.launch {
            repository.addNote(
                title = title,
                content = content,
                userId = userId,
                imageUrl = imageUrl,
                audioUrl = audioUrl,
                location = location,
                city = city
            )
        }
    }

    fun getNoteById(noteId: String) {
        viewModelScope.launch {
            _noteState.value = repository.getNoteById(noteId)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
            loadNotes(note.userId)
        }
    }
}
