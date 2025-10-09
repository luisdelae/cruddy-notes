package com.luisd.cruddynotes.ui.notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.luisd.cruddynotes.domain.model.Note
import com.luisd.cruddynotes.domain.model.SortOption
import com.luisd.cruddynotes.domain.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: NoteRepository

    private val timeStamp = System.currentTimeMillis()

    private val notes = listOf(
        Note(
            id = "3",
            title = "Cappa",
            content = "content3",
            category = "category3",
            timeStamp = timeStamp
        ),
        Note(
            id = "1",
            title = "Alpha",
            content = "content1",
            category = "category1",
            timeStamp = timeStamp
        ),
        Note(
            id = "4",
            title = "Delta",
            content = "content4",
            category = "category4",
            timeStamp = timeStamp
        ),
        Note(
            id = "2",
            title = "Beta",
            content = "content2",
            category = "category2",
            timeStamp = timeStamp
        )
    )

    private val categories = listOf("category1", "category2", "category3", "category4")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        coEvery { repository.getAllNotes() } returns flowOf(listOf())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading then transitions to Empty`() = runTest {
        val viewModel = NoteViewModel(repository)

        assert(viewModel.uiState.value is Loading)

        testDispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.uiState.value is Empty)
    }

    @Test
    fun `when repository returns notes, state is Content with notes`() = runTest {
        val notes = listOf(
            Note(title = "title", content = "content", category = "category")
        )
        coEvery { repository.getAllNotes() } returns flowOf(notes)

        val viewModel = NoteViewModel(repository)

        testDispatcher.scheduler.advanceUntilIdle()

        val contentState = Content(
            notes = notes,
            categories = listOf("category")
        )

        assertEquals(contentState, viewModel.uiState.value)
    }

    @Test
    fun `search filters notes by title`() = runTest {
        coEvery { repository.getAllNotes() } returns flowOf(notes)

        val viewModel = NoteViewModel(repository)

        viewModel.updateSearchQuery("delta")

        testDispatcher.scheduler.advanceUntilIdle()

        val contentState = Content(
            notes = listOf(
                Note(
                    id = "4",
                    title = "Delta",
                    content = "content4",
                    category = "category4",
                    timeStamp = timeStamp
                )
            ),
            categories = categories,
            isSearchActive = true
        )

        assertEquals(contentState, viewModel.uiState.value)
    }

    @Test
    fun `search filters notes by content`() = runTest {
        coEvery { repository.getAllNotes() } returns flowOf(notes)

        val viewModel = NoteViewModel(repository)

        viewModel.updateSearchQuery("content2")

        testDispatcher.scheduler.advanceUntilIdle()

        val contentState = Content(
            notes = listOf(
                Note(
                    id = "2",
                    title = "Beta",
                    content = "content2",
                    category = "category2",
                    timeStamp = timeStamp
                )
            ),
            categories = categories,
            isSearchActive = true
        )

        assertEquals(contentState, viewModel.uiState.value)
    }

    @Test
    fun `category filter shows only matching notes`() = runTest {
        val additionalNote = Note(
            id = "5",
            title = "Epsilon",
            content = "content5",
            category = "category3",
            timeStamp = timeStamp
        )
        val updatedNotes = notes + additionalNote

        coEvery { repository.getAllNotes() } returns flowOf(updatedNotes)

        val viewModel = NoteViewModel(repository)

        viewModel.updateSelectedCategory("category3")

        testDispatcher.scheduler.advanceUntilIdle()

        val contentState = Content(
            notes = listOf(
                Note(
                    id = "3",
                    title = "Cappa",
                    content = "content3",
                    category = "category3",
                    timeStamp = timeStamp
                ), additionalNote
            ),
            categories = categories,
            isSearchActive = false
        )

        assertEquals(contentState, viewModel.uiState.value)
    }

    @Test
    fun `sort by title descending orders correctly`() = runTest {
        coEvery { repository.getAllNotes() } returns flowOf(notes)

        val viewModel = NoteViewModel(repository)

        viewModel.updateSortOrder(SortOption.TITLE_ASC)

        testDispatcher.scheduler.advanceUntilIdle()

        val sortedNotes = notes.sortedBy { it.title }

        val contentState = Content(
            notes = sortedNotes,
            categories = categories,
            isSearchActive = false
        )

        assertEquals(contentState, viewModel.uiState.value)
    }

    @Test
    fun `deleteNote calls repository deleteNote`() = runTest {
        val viewmodel = NoteViewModel(repository)

        testDispatcher.scheduler.advanceUntilIdle()

        viewmodel.deleteNote("1")

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.deleteNote("1") }
    }
}