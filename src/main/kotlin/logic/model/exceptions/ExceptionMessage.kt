package org.example.logic.model.exceptions

enum class ExceptionMessage(
    val message: String
) {
    READ_EXCEPTION_MESSAGE("Error while reading data!"),
    WRITE_EXCEPTION_MESSAGE("Error while writing data!"),
    FILE_NOT_EXIST_EXCEPTION_MESSAGE("Error file not found!"),
    NOT_ALLOWED_STATE_NAME_MESSAGE("Only letters are allowed!"),
    EMPTY_DATA_MESSAGE("No data available"),
    STATE_NOT_EXIST_MESSAGE("The state not exist"),
    STATE_ALREADY_EXIST_MESSAGE("The state is already exist!"),
    STATE_NAME_LENGTH_MESSAGE("The state name is too long!"),
    INVALID_INPUT("Error Not valid input!"),
    NO_STATE_FOUNDED_MESSAGE("There is no states!!")

}