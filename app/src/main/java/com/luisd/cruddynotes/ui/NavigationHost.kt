package com.luisd.cruddynotes.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.luisd.cruddynotes.ui.notes.AddEditNoteScreen
import com.luisd.cruddynotes.ui.notes.NoteViewModel
import com.luisd.cruddynotes.ui.notes.NotesScreen

@Composable
fun NavigationHost(modifier: Modifier) {
    val navController = rememberNavController()

    val context = LocalContext.current

    val notesViewModel: NoteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(model: Class<T>): T {
                return NoteViewModel(context.applicationContext as Application) as T
            }
        }
    )

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NotesScreen
    ) {
        composable<NotesScreen> {
            NotesScreen(
                viewModel = notesViewModel,
                onNavigateToEditNote = { noteId ->
                    navController.navigate(AddEditNotesScreen(noteId = noteId))
                },
                onNavigateToAddNote = { navController.navigate(AddEditNotesScreen(noteId = null)) }
            )
        }
        composable<AddEditNotesScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<AddEditNotesScreen>()
            AddEditNoteScreen(
                viewModel = notesViewModel,
                noteId = args.noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}