package com.example.digitaldiary.view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.text.BasicText
import coil.compose.rememberAsyncImagePainter
import kotlin.math.roundToInt

@Composable
fun ImageDrawingScreen(imageUri: Uri, onSave: (Uri) -> Unit) {
    var text by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        BasicText(
            text = text,
            modifier = Modifier.offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
        )
        Button(
            onClick = { onSave(imageUri) },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Save")
        }
    }
}