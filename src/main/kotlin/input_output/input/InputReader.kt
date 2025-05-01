package org.example.input_output.input

import kotlinx.datetime.LocalDateTime


interface InputReader {
    fun readStringOrNull(): String?
    fun readFloatOrNull(): Float?
    fun readDateOrNull(): LocalDateTime?
    fun readIntOrNull(): Int?
}