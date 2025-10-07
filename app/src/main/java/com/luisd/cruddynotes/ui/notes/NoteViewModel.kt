package com.luisd.cruddynotes.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.luisd.cruddynotes.core.NotesApplication
import com.luisd.cruddynotes.domain.model.Note
import com.luisd.cruddynotes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Utilizing AndroidViewModel and passing the Application into it as this is a
// quick project that doesn't yet need DI. Would use in a larger project.
// Mid point to this would be to use a ViewModel factory, but not needed at the moment.
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository =
        (application as NotesApplication).repository

    // This way loses encapsulation and we can access the notesUiState directly from the caller
    // It is more performant as it does not trigger if the app is in the background
    // and we have a say in when to start observing instead of being done on init
//    val notesUiState: StateFlow<NotesViewState> = repository.getAllNotes()
//        .map { notes ->
//            Content(false, notes)
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000), // Stops observing when 5s pass after last subscriber disappears
//            initialValue = Loading
//        )

    private val _uiSTate = MutableStateFlow<NotesViewState>(Loading)

    val uiState: StateFlow<NotesViewState> = _uiSTate

    // This way may be less complex but it has drawbacks:
    // Always observing regardless of UI state
    // Collection states immediately on ViewModel creation
    // Continues to to collect when app is in background
    // Only stops when the VM is cleared
    // At the same time, we can keep the private/public pattern for better encapsulation
    init {
        viewModelScope.launch {
            repository.getAllNotes().collect { notes ->
                _uiSTate.update {
                    Content(loading = false, notes = notes)
                }
            }
        }
    }

    fun saveNote(
        noteId: String? = null,
        title: String,
        content: String,
        category: String
    ) {
        viewModelScope.launch {
            val note = if (noteId == null) {
                Note(title = title, content = content, category = category)
            } else {
                Note(id = noteId, title = title, content = content, category = category)
            }

            repository.insertNote(note)
        }
    }

    fun deleteNote(
        noteId: String
    ) {
        viewModelScope.launch {
            repository.deleteNote(noteId = noteId)
        }
    }
}