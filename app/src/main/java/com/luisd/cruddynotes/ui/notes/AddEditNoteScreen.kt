package com.luisd.cruddynotes.ui.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    viewModel: NoteViewModel,
    noteId: String?,
    onNavigateBack: () -> Unit
) {
    val noteToEdit = noteId?.let { id ->
        viewModel.uiState.collectAsState().value.let { state ->
            if (state is Content) {
                state.notes.find { it.id == id }
            } else null
        }
    }

    val title = rememberTextFieldState(initialText = noteToEdit?.title ?: "")
    val content = rememberTextFieldState(initialText = noteToEdit?.content ?: "")
    val category = rememberTextFieldState(initialText = noteToEdit?.category ?: "")

    AddEditNoteScreenContent(
        isNewNote = noteId == null,
        title = title,
        content = content,
        category = category,
        onButtonClick = {
            viewModel.saveNote(
                noteId = noteId,
                title = title.text.toString(),
                content = content.text.toString(),
                category = category.text.toString()
            )
        },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreenContent(
    isNewNote: Boolean,
    title: TextFieldState,
    content: TextFieldState,
    category: TextFieldState,
    onButtonClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNewNote) "Add Note" else "Edit Note") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NoteFields(title = title, content = content, category = category)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { onButtonClick() }
            ) {
                Text(text = "Save Note")
            }
        }
    }
}

@Composable
@Preview
fun AddEditNoteScreenContentPreview() {
    AddEditNoteScreenContent(
        isNewNote = false,
        title = TextFieldState("Get it done"),
        content = TextFieldState("This is the content of an existing note."),
        category = TextFieldState("Important"),
        onButtonClick = { },
        onNavigateBack = { }
    )
}

@Composable
fun NoteFields(
    title: TextFieldState,
    content: TextFieldState,
    category: TextFieldState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
    {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = title,
            label = { "Note title" },
            placeholder = { "Note title" }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = content,
            label = { "Note content" },
            placeholder = { "Note content" }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = category,
            label = { "Note category" },
            placeholder = { "Note category" }
        )
    }
}

@Composable
@Preview
fun NoteFieldsPreview() {
    NoteFields(
        title = TextFieldState("Get it done"),
        content = TextFieldState("This is the content of an existing note."),
        category = TextFieldState("Important")
    )
}