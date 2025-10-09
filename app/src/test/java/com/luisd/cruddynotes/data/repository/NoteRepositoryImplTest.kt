package com.luisd.cruddynotes.data.repository

import com.luisd.cruddynotes.data.local.NoteDao
import com.luisd.cruddynotes.domain.model.Note
import com.luisd.cruddynotes.domain.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.luisd.cruddynotes.data.local.Note as NoteEntity

class NoteRepositoryImplTest {
    private lateinit var dao: NoteDao

    private val timeStamp = System.currentTimeMillis()
    private lateinit var repository: NoteRepository

    val entityNotes = listOf(
        NoteEntity(
            id = "1",
            title = "Alpha",
            content = "content 1",
            category = "category1",
            timeStamp = timeStamp
        ),
        NoteEntity(
            id = "2",
            title = "Beta",
            content = "content 1",
            category = "category2",
            timeStamp = timeStamp
        )
    )

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = NoteRepositoryImpl(dao)
    }

    @Test
    fun `getAllNotes returns mapped domain models`() = runTest {
        coEvery { dao.getAllNotes() } returns flowOf(entityNotes)

        val result = repository.getAllNotes().first()

        val expectedNotes = listOf(
            Note(
                id = "1",
                title = "Alpha",
                content = "content 1",
                category = "category1",
                timeStamp = timeStamp
            ),
            Note(
                id = "2",
                title = "Beta",
                content = "content 1",
                category = "category2",
                timeStamp = timeStamp
            )
        )

        assertEquals(2, result.size)
        assertEquals(expectedNotes[0], result[0])
        assertEquals(expectedNotes[1], result[1])
    }

    @Test
    fun `insertNote maps and inserts entity`() = runTest {
        val noteToInsert =
            Note(
                id = "1",
                title = "Alpha",
                content = "content 1",
                category = "category1",
                timeStamp = timeStamp
            )

        repository.insertNote(noteToInsert)

        coVerify(exactly = 1) {
            dao.insertNote(
                match { entity ->
                    entity.id == "1" &&
                    entity.title == "Alpha" &&
                    entity.content == "content 1" &&
                    entity.category == "category1" &&
                    entity.timeStamp == timeStamp
                }
            )
        }
    }

    @Test
    fun `deleteNote calls dao delete`() = runTest {
        repository.deleteNote(noteId = "1")

        coVerify(exactly = 1) {
            dao.deleteNote(noteId = "1")
        }
    }
}