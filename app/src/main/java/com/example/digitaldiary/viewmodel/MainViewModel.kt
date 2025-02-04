package com.example.digitaldiary.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.example.digitaldiary.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _note = MutableLiveData<String>()
    val note: LiveData<String> = _note

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    fun onNoteChange(newNote: String) {
        _note.value = newNote
    }

    fun captureLocation() {
        // Użycie korutyn do obsługi lokalizacji
        CoroutineScope(Dispatchers.IO).launch {
            try {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        _location.postValue(task.result)
                    } else {
                        // Obsługa błędu pobierania lokalizacji
                    }
                })
            } catch (e: SecurityException) {
                // Obsługa wyjątku
            }
        }
    }

    fun addImage() {
        // Logika do dodawania zdjęcia
    }

    fun recordAudio() {
        // Logika do nagrywania dźwięku
    }

    fun submitNote() {
        // Logika do zapisu notatki
    }
}
