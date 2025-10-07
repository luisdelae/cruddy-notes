package com.luisd.cruddynotes.data.local

import android.content.Context
import androidx.room.Database;
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    // Singleton pattern here so that it only returns one database instance
    companion object {
        // Do a bit more research on this @Volatile
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        // Synchronized prevents race conditions when multiple threads have access
        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
