package com.example.digitaldiary.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.digitaldiary.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class NoteRepository {

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes")

    fun getNotesByUser(userId: String): Query {
        return notesCollection.whereEqualTo("userId", userId)
    }

    suspend fun addNote(note: Note) {
        notesCollection.add(note).await()
    }

    suspend fun updateNote(noteId: String, title: String, content: String) {
        val noteRef = notesCollection.document(noteId)
        noteRef.update("title", title, "content", content).await()
    }

    fun getNoteById(noteId: String): LiveData<Note> {
        val liveData = MutableLiveData<Note>()
        notesCollection.document(noteId).get().addOnSuccessListener { document ->
            if (document != null) {
                liveData.value = document.toObject(Note::class.java)
            }
        }
        return liveData
    }
}
