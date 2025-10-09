package com.luisd.cruddynotes.domain.model

enum class SortOption(val displayValue: String) {
    DATE_DESC(displayValue = "Newest first"),
    DATE_ASC(displayValue = "Oldest first"),
    TITLE_ASC(displayValue = "Title A-Z"),
    CATEGORY_ASC(displayValue = "Category A-Z")
}

fun SortOption.comparator(): Comparator<Note> = when (this) {
    SortOption.DATE_DESC -> compareByDescending { it.timeStamp }
    SortOption.DATE_ASC -> compareBy { it.timeStamp }
    SortOption.TITLE_ASC -> compareBy { it.title }
    SortOption.CATEGORY_ASC -> compareBy { it.category }
}