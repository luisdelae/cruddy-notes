package com.luisd.cruddynotes.ui.notes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.cruddynotes.domain.model.Note
import com.luisd.cruddynotes.domain.model.SortOption
import com.luisd.cruddynotes.domain.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NoteViewModel,
    onNavigateToAddNote: () -> Unit,
    onNavigateToEditNote: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val selectedSortOption by viewModel.selectedSortOrder.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddNote() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note",
                )
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is Content -> {
                val state = (uiState as Content)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    CategoryList(
                        categories = state.categories,
                        selectedCategory = selectedCategory
                    ) { newCategorySelection ->
                        viewModel.updateSelectedCategory(newCategorySelection)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SearchBar(
                            Modifier.weight(1f),
                            searchQuery
                        ) { query -> viewModel.updateSearchQuery(query) }
                        Spacer(Modifier.width(8.dp))
                        SortMenu(selectedSortOption) { option -> viewModel.updateSortOrder(option) }
                    }

                    if (state.notes.isEmpty() && state.isSearchActive) {
                        EmptySearchResults()
                    } else if (state.notes.isEmpty()) {
                        // Should not be here
                    } else {
                        NoteCardList(
                            notes = state.notes,
                            sortOption = selectedSortOption,
                            onSwipe = { noteId -> viewModel.deleteNote(noteId) },
                            onNavigateToEditNote = onNavigateToEditNote
                        )
                    }
                }
            }

            is Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error loading notes: \n${(uiState as Error).error}")
                }
            }

            Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            Empty -> {
                NotesEmpty(paddingValues)
            }
        }
    }
}

@Composable
fun NoteCardList(
    notes: List<Note>,
    sortOption: SortOption,
    onSwipe: (String) -> Unit,
    onNavigateToEditNote: (String) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(sortOption) {
        listState.scrollToItem(0)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(
            items = notes,
            key = { note -> note.id }
        ) { note ->
            NoteItemSwipeable(
                note = note,
                onSwipe = { onSwipe(note.id) },
                onClick = { onNavigateToEditNote(note.id) }
            )
        }
    }
}

@Composable
@Preview
fun NoteCardListPreview() {
    val textNote1 =
        Note(
            id = "123",
            title = "Test title One",
            content = "This is a test note with a couple of lines to text to " +
                    "ensure that it works correctly.",
            category = "Testing",
            timeStamp = 1759766563000
        )

    val textNote2 =
        Note(
            id = "456",
            title = "Test title Two",
            content = "Testing just a single line",
            category = "Short",
            timeStamp = 1759766563000
        )

    val textNote3 =
        Note(
            id = "789",
            title = "Test title Three but pretty long ",
            content = "This is a test note with a couple of lines to text to " +
                    "ensure that it works correctly.\n\nLet's make sure forced multi line" +
                    "works as well as it should.\n\n\nIt do!",
            category = "Looooooooooooooooooong",
            timeStamp = 1759766563000
        )
    val notes = listOf(
        textNote1, textNote2, textNote3
    )
    NoteCardList(
        notes = notes,
        sortOption = SortOption.DATE_DESC,
        onSwipe = { },
        onNavigateToEditNote = { }
    )
}

@Composable
fun NotesEmpty(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NoteAlt,
            contentDescription = "No notes icon",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(48.dp)
        )
        Text(
            text = "No notes yet. Press the + to add a note!",
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
fun NotesEmptyPreview() {
    NotesEmpty(PaddingValues(0.dp))
}

@Composable
fun NoteItemSwipeable(
    note: Note,
    onSwipe: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold,
        )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue =
                    if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd)
                        Color.Red
                    else Color.Transparent,
                animationSpec = tween(durationMillis = 200)
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        modifier = Modifier
            .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
            .clickable(onClick = onClick),
        onDismiss = { onSwipe() },
        enableDismissFromEndToStart = false
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(10.dp)
        ) {
            Text(
                text = note.title,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = note.content)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = note.timeStamp.toFormattedString(),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )

                AssistChip(
                    modifier = Modifier.widthIn(max = 100.dp),
                    onClick = { },
                    enabled = true,
                    label = {
                        Text(
                            text = note.category,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun NoteItemSwipeablePreview() {
    val testNote =
        Note(
            id = "123",
            title = "Test title",
            content = "This is a test note with a couple of lines to text to " +
                    "ensure that it works correctly.\n\nLet's make sure forced multi line" +
                    "works as well as it should.\n\n\nIt do!",
            category = "Testing",
            timeStamp = 1759766563000
        )

    NoteItemSwipeable(testNote, { }, onClick = { })
}

@Composable
fun SearchBar(
    modifier: Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search notes...") },
        leadingIcon = { Icon(Icons.Default.Search, "search") }
    )
}

@Composable
fun SortMenu(
    selectedSortOption: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(Icons.AutoMirrored.Default.Sort, contentDescription = "Sort options")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = true)
        ) {
            SortOption.entries.forEach { sortOption ->
                DropdownMenuItem(
                    text = { Text(sortOption.displayValue) },
                    onClick = {
                        onSortChange(sortOption)
                        expanded = false
                    },
                    trailingIcon = {
                        if (sortOption == selectedSortOption) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EmptySearchResults() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = "No results icon",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(48.dp)
        )
        Text(
            text = "No notes found.\nTry searching for something else!",
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
fun EmptySearchResultsPreview() {
    EmptySearchResults()
}

@Composable
fun CategoryList(
    categories: List<String>,
    selectedCategory: String?,
    onSelectedCategoryChange: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        item {
            InputChip(
                selected = selectedCategory == null,
                onClick = { onSelectedCategoryChange(null) },
                label = { Text("All") }
            )
            Spacer(Modifier.width(8.dp))
        }
        items(categories) { category ->
            InputChip(
                selected = category == selectedCategory,
                onClick = { onSelectedCategoryChange(category) },
                label = { Text(category) }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}