package com.example.digitaldiary.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.digitaldiary.viewmodel.NoteViewModel

@Composable
fun AppNavHost(navController: NavHostController, viewModel: NoteViewModel) {
    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            PreviousNotesScreen(navController = navController, viewModel = viewModel, userId = "defaultUserId")
        }
        composable("editNote/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            EditNoteScreen(navController = navController, viewModel = viewModel, noteId = noteId)
        }
    }
}
