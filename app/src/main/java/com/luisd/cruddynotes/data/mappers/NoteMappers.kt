package com.luisd.cruddynotes.data.mappers

import com.luisd.cruddynotes.data.local.Note as NoteEntity
import com.luisd.cruddynotes.domain.model.Note as NoteDomain

fun NoteEntity.toDomainModel() = NoteDomain(
    id = id,
    title = title,
    content = content,
    category = category,
    timeStamp = timeStamp
)

fun NoteDomain.toEntityModel() = NoteEntity(
    id = id,
    title = title,
    content = content,
    category = category,
    timeStamp = timeStamp
)