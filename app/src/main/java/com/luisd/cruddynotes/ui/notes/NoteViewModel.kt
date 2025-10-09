package com.luisd.cruddynotes.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.luisd.cruddynotes.core.NotesApplication
import com.luisd.cruddynotes.domain.model.Note
import com.luisd.cruddynotes.domain.model.SortOption
import com.luisd.cruddynotes.domain.model.SortOption.DATE_DESC
import com.luisd.cruddynotes.domain.model.comparator
import com.luisd.cruddynotes.domain.repository.NoteRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Utilizing AndroidViewModel and passing the Application into it as this is a
// quick project that doesn't yet need DI. Would use in a larger project.
@OptIn(FlowPreview::class)
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository =
        (application as NotesApplication).repository

    private val _uiState = MutableStateFlow<NotesViewState>(Loading)
    val uiState: StateFlow<NotesViewState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _selectedSortOrder = MutableStateFlow<SortOption>(DATE_DESC)
    val selectedSortOrder = _selectedSortOrder

    init {
        viewModelScope.launch {
            try {
                combine(
                    repository.getAllNotes(),
                    _searchQuery.debounce(300),
                    _selectedCategory,
                    _selectedSortOrder
                ) { notes, query, category, sortOrder ->
                    if (notes.isEmpty() && query.isBlank()) {
                        Empty
                    } else {
                        val displayNotes = notes
                            .filter { note -> category == null || note.category == category }
                            .filter { note ->
                                query.isBlank() ||
                                        note.title.contains(query, ignoreCase = true) ||
                                        note.content.contains(query, ignoreCase = true)
                            }.sortedWith(sortOrder.comparator())

                        val categories = notes.map { it.category }.distinct().sorted()

                        Content(
                            notes = displayNotes,
                            categories = categories,
                            isSearchActive = query.isNotBlank()
                        )
                    }
                }.collect { newState -> _uiState.update { newState } }
            } catch (e: Exception) {
                _uiState.update { Error(e.toString()) }
            }
        }
    }

    fun saveNote(
        noteId: String? = null,
        title: String,
        content: String,
        category: String
    ) {
        // Rudimentary validation
        if (title.isEmpty() || content.isEmpty()) {
            return
        }

        viewModelScope.launch {
            val note = if (noteId == null) {
                Note(title = title, content = content, category = category)
            } else {
                Note(id = noteId, title = title, content = content, category = category)
            }

            repository.insertNote(note)
        }
    }

    fun deleteNote(
        noteId: String
    ) {
        viewModelScope.launch {
            repository.deleteNote(noteId = noteId)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun updateSortOrder(sortEnum: SortOption) {
        _selectedSortOrder.value = sortEnum
    }
}