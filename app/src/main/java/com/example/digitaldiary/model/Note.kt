package com.example.digitaldiary.model

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val userId: String = "",
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val location: String? = null
)
