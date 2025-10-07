# Cruddy Notes

A modern Android notes application demonstrating Clean Architecture principles and contemporary Android development patterns. 
Built as a technical demonstration project focused on architectural best practices and modern reactive programming.

## Purpose

This project serves as a practical implementation of Clean Architecture on Android, showcasing:
- Proper separation of concerns across domain, data, and presentation layers
- Modern reactive programming with Kotlin Flow and Coroutines
- Type-safe navigation with Jetpack Compose
- Repository pattern with clear abstraction boundaries
- State management patterns for complex UI interactions

## Features

- ✅ Create, read, update, and delete notes
- ✅ Categorize notes with custom categories
- ✅ Swipe-to-delete functionality
- 🔳 Search notes by title and content (debounced)
- 🔳 Filter notes by category
- 🔳 Sort notes by date, title, or category
- ✅ Offline-first architecture with Room database
- ✅ Material Design 3 UI with Jetpack Compose

## Tech Stack

### Core Technologies
- **Language**: Kotlin
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 36 (Android 16)

### Architecture & Patterns
- **Architecture**: Clean Architecture (Domain/Data/Presentation layers)
- **Pattern**: Repository Pattern with interface abstraction
- **Dependency Injection**: Manual (Application-scoped)
- **State Management**: StateFlow with sealed classes

### Jetpack Libraries
- **UI**: Jetpack Compose with Material 3
- **Navigation**: Navigation Compose with type-safe routing
- **Persistence**: Room Database with Flow integration
- **Lifecycle**: ViewModel with lifecycle-aware state collection
- **Coroutines**: Kotlin Coroutines with structured concurrency

### Modern Android Patterns
- **Reactive Programming**: Kotlin Flow with operators (`combine`, `map`, `filter`, `debounce`)
- **Concurrency**: Coroutines with proper dispatcher usage
- **Type Safety**: Kotlinx Serialization for navigation arguments

## Development Timeline

- **Day 1**: Core CRUD operations, basic UI, Room integration
- **Day 2**: Search, filter, and sort functionality with Flow operators
- **Day 3**: Edge cases, error handling, input validation, polish