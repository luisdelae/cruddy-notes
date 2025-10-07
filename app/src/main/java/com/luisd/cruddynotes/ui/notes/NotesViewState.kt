package com.luisd.cruddynotes.ui.notes

import com.luisd.cruddynotes.domain.model.Note

sealed class NotesViewState

object Loading: NotesViewState()

object Error: NotesViewState()

data class Content(
    val loading: Boolean = false,
    val notes: List<Note> = emptyList()
) : NotesViewState()
