package com.example.digitaldiary.repository

import com.example.digitaldiary.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await




class NoteRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes")

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
                // Obsługa błędu
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
        note.id?.let {
            notesCollection.document(it).set(note).await()
        }
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
