package com.example.digitaldiary.repository

import com.example.digitaldiary.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NoteRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")

    suspend fun getAllNotes(userId: String): List<Note> {
        return try {
            notesCollection.whereEqualTo("userId", userId).get().await()
                .toObjects(Note::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addNote(
        title: String,
        content: String,
        userId: String,
        imageUrl: String? = null,
        audioUrl: String? = null,
        location: String? = null,
        city: String = ""
    ) {
        val note = Note(
            title = title,
            content = content,
            userId = userId,
            imageUrl = imageUrl,
            audioUrl = audioUrl,
            location = location,
            city = city
        )
        notesCollection.add(note).await()
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
}
