package com.example.digitaldiary.model

import com.google.firebase.firestore.DocumentId
import com.google.gson.internal.bind.TypeAdapters.UUID

//data class Note(
//    @DocumentId val id: String = "",
//    val title: String = "",
//    val content: String = "",
//    val userId: String = "",
//    val imageUrl: String? = null,
//    val audioUrl: String? = null,
//    val location: String? = null,
//    val timestamp: Long = System.currentTimeMillis(),
//    val city: String = ""
//)



data class Note(
    val id: String = "", // Firestore document ID
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = "",
    val noteId: String = UUID.randomUUID().toString()
)


