package org.example.data.extention

import kotlinx.datetime.LocalDateTime


//TODO
fun String.toLocalDateTime(): LocalDateTime = try {
    LocalDateTime.parse(this)
} catch (ex: IllegalArgumentException) {
    throw Exception("Invalid date format: $this")
}
