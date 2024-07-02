package com.example.digitaldiary.repository

import com.example.digitaldiary.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class NoteRepository {

    private val notes = MutableStateFlow<List<Note>>(emptyList())

    fun getNotes(userId: String): Flow<List<Note>> {
        // Implementacja pobierania notatek z odpowiednim userId
        return notes
    }

    fun addNote(note: Note) {
        val noteWithId = note.copy(id = UUID.randomUUID().toString())
        notes.value = notes.value + noteWithId
    }

    fun updateNote(note: Note) {
        notes.value = notes.value.map { if (it.id == note.id) note else it }
    }

    fun getNoteById(noteId: String): Flow<Note?> {
        // Implementacja pobierania notatki po noteId
        return MutableStateFlow(notes.value.find { it.id == noteId })
    }
}
