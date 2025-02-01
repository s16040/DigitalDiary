package com.example.digitaldiary.viewmodel


import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiary.model.Note
import com.example.digitaldiary.repository.NoteRepository
import com.example.digitaldiary.utils.MediaUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class NoteViewModel(private val repository: NoteRepository,private val context: Context) : ViewModel() {

    private val mediaUtils = MediaUtils(context)

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

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            if (note != null) {
                repository.deleteNote(noteId)
                loadNotes(note.userId)
            }
        }
    }

    fun updatePhotoUri(uri: String) {
        viewModelScope.launch {
            noteState.value?.let { note ->
                val firebaseUri = Uri.parse(uri)
                val firebaseUrl = mediaUtils.uploadMediaToFirebase(firebaseUri, "images")
                updateNote(note.copy(imageUrl = firebaseUrl))
            }
        }
    }

    fun updateAudioPath(audioPath: String) {
        viewModelScope.launch {
            try {
                val audioUri = Uri.fromFile(File(audioPath))
                val firebaseUrl = mediaUtils.uploadMediaToFirebase(audioUri, "audio")
                noteState.value?.let { note ->
                    updateNote(note.copy(audioUrl = firebaseUrl))
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    fun updateAudio(filePath: String) {
        viewModelScope.launch {
            val mediaUtils = MediaUtils(context)
            val firebaseUrl = mediaUtils.uploadAudioToFirebase(filePath)
            noteState.value?.let { note ->
                updateNote(note.copy(audioUrl = firebaseUrl))
            }
        }
    }
    fun updateImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val mediaUtils = MediaUtils(context)
                val firebaseUrl = mediaUtils.uploadImageToFirebase(uri)
                noteState.value?.let { note ->
                    updateNote(note.copy(imageUrl = firebaseUrl))
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
