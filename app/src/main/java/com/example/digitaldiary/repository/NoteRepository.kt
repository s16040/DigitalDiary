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

    fun getNotesByUser(userId: String): Flow<QuerySnapshot> = callbackFlow {
        val listener = notesCollection.whereEqualTo("userId", userId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else {
                snapshot?.let { trySend(it).isSuccess }
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun addNote(note: Note) {
        notesCollection.add(note).await()
    }

    suspend fun updateNote(note: Note) {
        notesCollection.document(note.id).set(note).await()
    }

    fun getNoteById(noteId: String): Flow<Note?> = callbackFlow {
        val listener = notesCollection.document(noteId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else {
                snapshot?.let { trySend(it.toObject(Note::class.java)).isSuccess }
            }
        }
        awaitClose { listener.remove() }
    }
}
