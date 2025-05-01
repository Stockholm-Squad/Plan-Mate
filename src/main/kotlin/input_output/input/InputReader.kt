package org.example.input_output.input

import kotlinx.datetime.LocalDateTime


interface InputReader {
    fun readStringOrNull(): String?
    fun readDateOrNull(): LocalDateTime?
    fun readFloatOrNull(): Float?
    fun readIntOrNull(): Int?
}