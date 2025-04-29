package org.example.logic.model.exceptions

enum class ExceptionMessage(
    val message: String
) {
    READ_EXCEPTION_MESSAGE("Error while reading data!"),
    WRITE_EXCEPTION_MESSAGE("Error while writing data!"),
    NOT_ALLOWED_STATE_NAME_MESSAGE("Only Letter Are Allowed!"),
    EMPTY_DATA_MESSAGE("Error! We Don't Have Data"),
    STATE_NOT_EXIST_EXCEPTION("The state not exist"),
}