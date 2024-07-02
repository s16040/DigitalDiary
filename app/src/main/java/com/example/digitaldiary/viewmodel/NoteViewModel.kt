package com.example.digitaldiary.viewmodel

import androidx.lifecycle.*
import com.example.digitaldiary.model.Note
import com.example.digitaldiary.repository.NoteRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> get() = _notes

    fun loadNotes(userId: String) {
        repository.getNotesByUser(userId).addSnapshotListener { value, error ->
            if (error != null) {
                _notes.value = emptyList()
                return@addSnapshotListener
            }
            _notes.value = value?.toObjects(Note::class.java)
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
            val noteContent = "$content\n#$city #${formatTimestamp(timestamp)}"

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

    fun updateNote(noteId: String, title: String, content: String) {
        viewModelScope.launch {
            repository.updateNote(noteId, title, content)
        }
    }

    fun getNoteById(noteId: String): LiveData<Note> {
        return repository.getNoteById(noteId).asLiveData()
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
