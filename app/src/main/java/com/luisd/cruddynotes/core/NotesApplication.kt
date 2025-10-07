package com.luisd.cruddynotes.core

import android.app.Application
import com.luisd.cruddynotes.data.local.NoteDatabase
import com.luisd.cruddynotes.data.repository.NoteRepositoryImpl
import com.luisd.cruddynotes.domain.repository.NoteRepository

class NotesApplication : Application() {
    val repository: NoteRepository by lazy {
        val database = NoteDatabase.getDatabase(this)
        NoteRepositoryImpl(database.noteDao())
    }
}