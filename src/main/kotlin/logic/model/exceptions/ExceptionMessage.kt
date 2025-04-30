package org.example.logic.model.exceptions

enum class ExceptionMessage(
    val message: String
) {
    READ_EXCEPTION_MESSAGE("Error while reading data!"),
    WRITE_EXCEPTION_MESSAGE("Error while writing data!"),
    FILE_NOT_EXIST_EXCEPTION_MESSAGE("Error file not found!"),
    NO_TASKS_FOUNDED("No Tasks founded"),
    TASK_CREATED_SUCCESSFULLY("Task Created Successfully"),
    FAILED_TO_CREATE_TASK("Failed to create task"),
    TASK_DELETED_SUCCESSFULLY("Task Deleted Successfully"),
    FAILED_TO_DELETE_TASK("Failed to delete task"),
    TASK_NOT_FOUND ( "Task not found"),
    TASK_EDITED_SUCCESSFULLY("Task Updated Successfully"),
    FAILED_TO_EDIT_TASK("Failed to update task"),
    NO_STATE_FOUND("No states found for the project."),
    TASK_ALREADY_EXISTS("task is existed")

}