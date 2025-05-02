package org.example.data.extention

import kotlinx.datetime.LocalDateTime
import java.util.*


fun String.toSafeUUID(): UUID = try {
    UUID.fromString(this)
} catch (ex: IllegalArgumentException) {
    throw Exception("Invalid UUID format: $this")
}

fun String.toLocalDateTime(): LocalDateTime = try {
    LocalDateTime.parse(this)
} catch (ex: IllegalArgumentException) {
    throw Exception("Invalid date format: $this")
}
