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
    NO_DATA_IN_THE_FILE("The file is empty"),
    NO_PROJECT_ADDED("No Project Added"),
    COULD_NOT_UPDATE_PROJECT("Couldn't update project"),
    NOT_ALLOWED_STATE_NAME_MESSAGE("Only letters are allowed!"),
    EMPTY_DATA_MESSAGE("No data available"),
    STATE_NOT_EXIST_MESSAGE("The state not exist"),
    STATE_ALREADY_EXIST_MESSAGE("The state is already exist!"),
    STATE_NAME_LENGTH_MESSAGE("The state name is too long!"),
    INVALID_INPUT("Error Not valid input!"),
    NO_STATE_FOUNDED_MESSAGE("There is no states!!"),
    INVALID_STATE_NAME_MESSAGE("Invalid state name!!"),

    USERS_IS_EMPTY("Users is empty")
}