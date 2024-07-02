package com.example.digitaldiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiary.model.Note
import com.example.digitaldiary.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> get() = _notes

    fun loadNotes(userId: String) {
        viewModelScope.launch {
            repository.getNotes(userId).collect {
                _notes.value = it
            }
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.addNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun getNoteById(noteId: String): Flow<Note?> {
        return repository.getNoteById(noteId)
    }
}
