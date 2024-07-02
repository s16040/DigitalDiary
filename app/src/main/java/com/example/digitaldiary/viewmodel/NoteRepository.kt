package com.example.digitaldiary.viewmodel

import com.example.digitaldiary.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class NoteRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes")

    fun addNote(note: Note) {
        notesCollection.add(note)
            .addOnSuccessListener { documentReference ->
                println("Note added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding note: $e")
            }
    }

    fun getNotes(callback: (List<Note>) -> Unit) {
        notesCollection.get()
            .addOnSuccessListener { result ->
                val notes = result.map { document -> document.toObject<Note>() }
                callback(notes)
            }
            .addOnFailureListener { e ->
                println("Error getting notes: $e")
                callback(emptyList())
            }
    }
}
