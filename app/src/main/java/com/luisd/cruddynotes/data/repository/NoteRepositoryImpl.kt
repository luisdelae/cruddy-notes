package com.luisd.cruddynotes.data.repository

import com.luisd.cruddynotes.data.local.NoteDao
import com.luisd.cruddynotes.data.mappers.toDomainModel
import com.luisd.cruddynotes.data.mappers.toEntityModel
import com.luisd.cruddynotes.domain.model.Note
import com.luisd.cruddynotes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {
    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes() .map {
        entityList -> entityList.map { it.toDomainModel() }
    }

    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note.toEntityModel())
    }

    override suspend fun deleteNote(noteId: String) {
        noteDao.deleteNote(noteId)
    }
}