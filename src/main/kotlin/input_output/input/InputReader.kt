package org.example.input_output.input

import kotlinx.datetime.LocalDate


interface InputReader {
    fun readStringOrNull(): String?
    fun readDateOrNull(): LocalDate?
    fun readFloatOrNull(): Float?
    fun readIntOrNull():Int?
}