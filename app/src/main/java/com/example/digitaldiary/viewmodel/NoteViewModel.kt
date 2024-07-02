package com.example.digitaldiary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.digitaldiary.model.Note

class NoteViewModel(application: Application) : ViewModel() {

    private val noteRepository = NoteRepository()
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> get() = _notes

    init {
        loadNotes()
    }

    private fun loadNotes() {
        noteRepository.getNotes { notesList ->
            _notes.value = notesList
        }
    }

    fun submitNote(noteText: String) {
        val newNote = Note(
            title = "New Note",
            content = noteText
        )
        noteRepository.addNote(newNote)
        loadNotes()
    }

    fun captureLocation() {
        // Implement capture location logic
    }

    fun addImage() {
        // Implement add image logic
    }

    fun recordAudio() {
        // Implement record audio logic
    }
}
