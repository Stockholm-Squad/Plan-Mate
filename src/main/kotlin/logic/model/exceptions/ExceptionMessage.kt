package org.example.logic.model.exceptions

enum class ExceptionMessage(
    val message: String
) {
    READ_EXCEPTION_MESSAGE("Error while reading data!"),
    WRITE_EXCEPTION_MESSAGE("Error while writing data!"),
    FILE_NOT_EXIST_EXCEPTION_MESSAGE("Error file not found!"),
}