package com.example.digitaldiary.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.digitaldiary.viewmodel.NoteViewModel

@Composable
fun MainNavGraph(navController: NavHostController, viewModel: NoteViewModel, userId: String, onLogout: () -> Unit) {
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(viewModel, userId, onLogout)
        }
        composable("previous_notes") {
            PreviousNotesScreen(navController, viewModel, userId)
        }
        composable("edit_note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            EditNoteScreen(navController, viewModel, noteId)
        }
    }
}
