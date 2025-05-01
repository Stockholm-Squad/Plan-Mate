package org.example.logic.model.exceptions

enum class ExceptionMessage(
    val message: String
) {
    READ_EXCEPTION_MESSAGE("Error while reading data!"),
    WRITE_EXCEPTION_MESSAGE("Error while writing data!"),
    NO_PROJECT_ADDED("No Project Added"),
    COULD_NOT_UPDATE_PROJECT("Couldn't update project"),
    FILE_NOT_EXIST_EXCEPTION_MESSAGE("Error file not found!"),
    NO_DATA_IN_THE_FILE("The file is empty"),
    NO_TASKS_FOUNDED("No Tasks founded"),
    FAILED_TO_DELETE_TASK("Failed to delete task"),
    TASK_ALREADY_EXISTS("task is existed"),
    FAILED_TO_CREATE_TASK("Failed to create task"),
    PROJECT_NOT_FOUND("Project not found"),
    NO_STATES_FOUND("No states found"),
    NO_TASKS_FOUND("No tasks found."),
    INVALID_STATE_NAME("Invalid state name. No matching state ID found."),
    NOT_ALLOWED_STATE_NAME_MESSAGE("Only letters are allowed!"),
    EMPTY_DATA_MESSAGE("No data available"),
    STATE_NOT_EXIST_MESSAGE("The state not exist"),
    STATE_ALREADY_EXIST_MESSAGE("The state is already exist!"),
    STATE_NAME_LENGTH_MESSAGE("The state name is too long!"),
    NO_STATE_FOUNDED_MESSAGE("There is no states!!"),
    INVALID_STATE_NAME_MESSAGE("Invalid state name!!");
}