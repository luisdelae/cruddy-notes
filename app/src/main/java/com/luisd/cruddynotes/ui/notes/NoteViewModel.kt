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

    private val _uiSTate = MutableStateFlow<NotesViewState>(Loading)

    val uiState: StateFlow<NotesViewState> = _uiSTate

    init {
        viewModelScope.launch {
            try {
                repository.getAllNotes().collect { notes ->
                    _uiSTate.update {
                        Content(notes = notes)
                    }
                }
            } catch (e: Exception) {
                _uiSTate.update { Error(e.toString()) }
            }
        }
    }

    fun saveNote(
        noteId: String? = null,
        title: String,
        content: String,
        category: String
    ) {
        // Rudimentary validation
        if (title.isEmpty() || content.isEmpty()) {
            return
        }

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