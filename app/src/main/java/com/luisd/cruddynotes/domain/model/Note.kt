package com.luisd.cruddynotes.domain.model

import java.util.UUID

data class Note(
    val id: String  = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val category: String,
    val timeStamp: Long = System.currentTimeMillis()
)
