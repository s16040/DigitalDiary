package com.example.digitaldiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.digitaldiary.repository.NoteRepository

class NoteViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(NoteRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
