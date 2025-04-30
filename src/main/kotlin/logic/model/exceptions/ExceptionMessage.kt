package org.example.logic.model.exceptions

enum class ExceptionMessage(
    val message: String
) {
    READ_EXCEPTION_MESSAGE("Error while reading data!"),
    WRITE_EXCEPTION_MESSAGE("Error while writing data!"),
    FILE_NOT_EXIST_EXCEPTION_MESSAGE("Error file not found!"),
    NO_DATA_IN_THE_FILE("The file is empty"),
    NO_PROJECT_ADDED("No Project Added"),
    COULD_NOT_UPDATE_PROJECT("Couldn't update project")
}