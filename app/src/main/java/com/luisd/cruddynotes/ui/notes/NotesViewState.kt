package com.luisd.cruddynotes.ui.notes

import com.luisd.cruddynotes.domain.model.Note

sealed class NotesViewState
object Loading: NotesViewState()
object Empty: NotesViewState() // No notes in DB
data class Error(val error: String): NotesViewState()
data class Content(
    val notes: List<Note> = emptyList(),
    val categories: List<String> = emptyList(),
    val isSearchActive: Boolean = false
) : NotesViewState()
