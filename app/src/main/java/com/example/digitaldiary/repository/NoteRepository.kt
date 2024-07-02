package com.example.digitaldiary.repository

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
}
