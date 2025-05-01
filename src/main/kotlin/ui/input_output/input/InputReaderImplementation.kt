package org.example.ui.input_output.input

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime


class InputReaderImplementation : InputReader {

    override fun readStringOrNull(): String? {
        val input = readlnOrNull()
        return input?.takeUnless { it.isBlank() }
    }

    override fun readDateOrNull(): LocalDateTime? {
        val input = readlnOrNull()
        return try {
            input?.takeUnless { it.isBlank() }?.toLocalDateTime()
        } catch (e: Exception) {
            null
        }
    }

    override fun readFloatOrNull(): Float? {
        val input = readlnOrNull()
        return input?.toFloatOrNull()
    }

    override fun readIntOrNull(): Int? {
        val input = readlnOrNull()
        return input?.toIntOrNull()
    }
}
