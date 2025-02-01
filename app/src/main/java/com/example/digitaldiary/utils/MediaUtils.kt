package com.example.digitaldiary.utils

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

class MediaUtils(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private val storage = FirebaseStorage.getInstance()

    fun startRecording(): String {
        val fileName = "${UUID.randomUUID()}.mp3"
        audioFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)
            prepare()
            start()
        }
        
        return fileName
    }

    fun stopRecording(): String? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            audioFile?.absolutePath
        } catch (e: Exception) {
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
}