package com.example.digitaldiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiary.model.Note
import com.example.digitaldiary.repository.NoteRepository
import kotlinx.coroutines.launch

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

    fun addNote(title: String, content: String, userId: String) {
        val note = Note(
            title = title,
            content = content,
            userId = userId
        )
        viewModelScope.launch {
            repository.addNote(note)
        }
    }
}
