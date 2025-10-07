package com.luisd.cruddynotes.ui.notes

import com.luisd.cruddynotes.domain.model.Note

sealed class NotesViewState

object Loading: NotesViewState()

data class Error(
    val error: String
): NotesViewState()

data class Content(
    val notes: List<Note> = emptyList()
) : NotesViewState()
