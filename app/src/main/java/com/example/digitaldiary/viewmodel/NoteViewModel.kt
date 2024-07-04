package com.example.digitaldiary.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiary.model.Note
import com.example.digitaldiary.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _noteState = MutableStateFlow<Note?>(null)
    val noteState: StateFlow<Note?> = _noteState

//    private val _notes = MutableLiveData<List<Note>>()
//    val notes: LiveData<List<Note>> get() = _notes

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val _audioPath = MutableLiveData<String?>()
    val audioPath: LiveData<String?> get() = _audioPath


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

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            if (note != null) {
                repository.deleteNote(noteId)
                loadNotes(note.userId)
            }
        }
    }
    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun setAudioPath(path: String?) {
        _audioPath.value = path
    }
}
