package org.example.logic.model.exceptions

enum class ExceptionMessage(
    val message: String
) {
    READ_EXCEPTION_MESSAGE("Error while reading data!"),
    WRITE_EXCEPTION_MESSAGE("Error while writing data!"),
    NO_TASKS_FOUNDED("No Tasks founded"),
    TASK_CREATED_SUCCESSFULLY("Task Created Successfully"),
    FAILED_TO_CREATE_TASK("Failed to create task")

}