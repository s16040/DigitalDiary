package com.example.digitaldiary.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

class MediaUtils(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var currentAudioFile: File? = null
    private val storage = FirebaseStorage.getInstance()
    private fun hasPermissions(): Boolean {
        return context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun startRecording(): String? {
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val audioFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            "audio_${System.currentTimeMillis()}.mp3"
        )
        currentAudioFile = audioFile

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile.absolutePath)
            prepare()
            start()
        }
        return audioFile.absolutePath
    }

    fun stopRecording(): String? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            currentAudioFile?.absolutePath
        } catch (e: Exception) {
            Log.e("MediaUtils", "Błąd podczas zatrzymywania nagrania", e)
            null
        }
    }

    suspend fun uploadAudioToFirebase(filePath: String): String? {
        return try {
            val file = File(filePath)
            val fileName = "audio_${System.currentTimeMillis()}.mp3"
            val ref = storage.reference.child("audio/$fileName")
            ref.putFile(Uri.fromFile(file)).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("MediaUtils", "Błąd przy przesyłaniu audio", e)
            null
        }
    }

    suspend fun uploadMedia(uri: Uri, type: String): String {
        val ref = storage.reference.child("$type/${UUID.randomUUID()}")
        return try {
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun uploadImageToFirebase(uri: Uri): String? {
        return try {
            val filename = "img_${System.currentTimeMillis()}.jpg"
            val ref = storage.reference.child("images/$filename")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("MediaUtils", "Błąd przy przesyłaniu obrazu", e)
            null
        }
    }

    suspend fun uploadMediaToFirebase(uri: Uri, type: String): String {
        val storageRef = storage.reference
        val mediaRef = storageRef.child("$type/${UUID.randomUUID()}")
        return try {
            mediaRef.putFile(uri).await()
            mediaRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw e
        }
    }
}
