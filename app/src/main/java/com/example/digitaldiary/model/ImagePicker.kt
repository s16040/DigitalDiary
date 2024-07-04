package com.example.digitaldiary.model

import android.app.Activity
import android.content.Intent
import android.net.Uri

class ImagePicker(private val activity: Activity) {

    fun pickImage(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        activity.startActivityForResult(intent, requestCode)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?, onImagePicked: (Uri) -> Unit) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                onImagePicked(it)
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}