package com.example.digitaldiary.model

data class Note(
    val id: String = "",
    val text: String = "",
    val location: String = "",
    val imageUrl: String? = null,
    val audioUrl: String? = null
)
//ver 3.0.