package com.luisd.cruddynotes.domain.repository

import com.luisd.cruddynotes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(noteId: String)
}