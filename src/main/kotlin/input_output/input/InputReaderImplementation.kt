package org.example.input_output.input


class InputReaderImplementation() : InputReader {
    override fun readStringOrNull(): String? {
        return readlnOrNull()
    }

    override fun readFloatOrNull(): Float? {
        return readlnOrNull()?.toFloatOrNull()
    }

    override fun readIntOrNull(): Int? {
        return readlnOrNull()?.toIntOrNull()
    }

}