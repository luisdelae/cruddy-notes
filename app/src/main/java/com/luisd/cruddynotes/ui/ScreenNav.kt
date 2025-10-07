package com.luisd.cruddynotes.ui

import kotlinx.serialization.Serializable

@Serializable
object NotesScreen
@Serializable
data class AddEditNotesScreen(val noteId: String? = null)
