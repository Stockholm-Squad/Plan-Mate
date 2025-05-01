package org.example.input_output.input


interface InputReader {
    fun readStringOrNull(): String?
    fun readFloatOrNull(): Float?
    fun readIntOrNull(): Int?
}