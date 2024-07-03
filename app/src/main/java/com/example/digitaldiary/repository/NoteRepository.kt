package com.example.digitaldiary.repository

import com.example.digitaldiary.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NoteRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")

    fun addNote(
        title: String,
        content: String,
        userId: String,
        imageUrl: String? = null,
        audioUrl: String? = null,
        location: String? = null,
        city: String = ""
    ) {
        val note = hashMapOf(
            "title" to title,
            "content" to content,
            "userId" to userId,
            "imageUrl" to imageUrl,
            "audioUrl" to audioUrl,
            "location" to location,
            "timestamp" to System.currentTimeMillis(),
            "city" to city
        )

        notesCollection.add(note)
            .addOnSuccessListener { documentReference ->
                val noteId = documentReference.id
                notesCollection.document(noteId).update("id", noteId)
            }
            .addOnFailureListener { e ->
                // Obsługa błędu?
            }
    }

    suspend fun getNoteById(noteId: String): Note? {
        return try {
            val documentSnapshot = notesCollection.document(noteId).get().await()
            documentSnapshot.toObject(Note::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateNote(note: Note) {
        notesCollection.document(note.id).set(note).await()
    }

    suspend fun deleteNote(noteId: String) {
        notesCollection.document(noteId).delete().await()
    }

    suspend fun getAllNotes(userId: String): List<Note> {
        return try {
            val querySnapshot = notesCollection.whereEqualTo("userId", userId).get().await()
            querySnapshot.toObjects(Note::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
