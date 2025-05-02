package org.example.data.mapper

import logic.model.entities.User
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.*


fun String.toSafeUUID(): UUID = try {
    UUID.fromString(this)
} catch (ex: IllegalArgumentException) {
    throw Exception("Invalid UUID format: $this")
}

fun String.toUser(): User = try {
    Json.decodeFromString<User>(this)
} catch (ex: SerializationException) {
    throw Exception("Failed to parse user data: ${ex.message}")
}

fun String.toLocalDateTime(): LocalDateTime = try {
    LocalDateTime.parse(this)
} catch (ex: IllegalArgumentException) {
    throw Exception("Invalid date format: $this")
}




