package org.example.logic.model.exceptions

enum class ExceptionMessage(
    val message: String
) {
    READ_EXCEPTION_MESSAGE("Error while reading data!"),
    WRITE_EXCEPTION_MESSAGE("Error while writing data!"),
    INVALID_USER_NAME("Invalid userName"),
    INVALID_PASSWORD("Invalid password"),
    USER_DOES_NOT_EXIST("User dose not exist"),
    USER_EXIST("User is exist"),
    INCORRECT_PASSWORD("Incorrect password"),
    FILE_NOT_EXIST_EXCEPTION_MESSAGE("Error file not found!"),
    USERS_IS_EMPTY("Users is empty")
}