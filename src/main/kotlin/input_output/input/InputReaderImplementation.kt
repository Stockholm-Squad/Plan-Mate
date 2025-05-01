package org.example.input_output.input

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime


class InputReaderImplementation : InputReader {

    override fun readStringOrNull(): String? {
        val input = readLine()
        return input?.takeUnless { it.isBlank() }
    }

    override fun readDateOrNull(): LocalDateTime? {
        val input = readLine()
        return try {
            input?.takeUnless { it.isBlank() }?.toLocalDateTime()
        } catch (e: Exception) {
            null
        }
    }

    override fun readFloatOrNull(): Float? {
        val input = readLine()
        return input?.toFloatOrNull()
    }

    override fun readIntOrNull(): Int? {
        val input = readLine()
        return input?.toIntOrNull()
    }
}
