package com.example.digitaldiary.repository

import com.example.digitaldiary.model.Note
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NoteRepository {

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes")

    fun getNotesByUser(userId: String): Query {
        return notesCollection.whereEqualTo("userId", userId)
    }

    suspend fun addNote(note: Note) {
        notesCollection.add(note).await()
    }

    fun getNoteById(noteId: String): Flow<DocumentSnapshot?> = flow {
        val document = notesCollection.document(noteId).get().await()
        emit(document)
    }


    suspend fun updateNote(note: Note) {
        notesCollection.document(note.id).set(note).await()
    }
}
