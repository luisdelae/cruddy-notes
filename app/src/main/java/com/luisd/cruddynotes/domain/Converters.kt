package com.luisd.cruddynotes.domain

import java.text.SimpleDateFormat
import java.util.Date

fun Long.toFormattedString(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
    return format.format(date)
}