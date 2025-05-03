package org.example.logic.usecase.extention

import java.util.UUID

fun String.isValidLength(length: Int): Boolean {
    return this.length <= length
}

 fun String.isLetterOrWhiteSpace(): Boolean {
    return this.all { char -> char.isLetter() || char.isWhitespace() }
}

fun String.toSafeUUID(): UUID = try {
    UUID.fromString(this)
} catch (ex: IllegalArgumentException) {
    throw Exception("Invalid UUID format: $this")
}
