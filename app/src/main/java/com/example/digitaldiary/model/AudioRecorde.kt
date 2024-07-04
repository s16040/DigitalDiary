package com.example.digitaldiary.model

import android.media.MediaRecorder

class AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var output: String = ""

    fun startRecording(filePath: String) {
        output = filePath
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(output)
            prepare()
            start()
        }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
}