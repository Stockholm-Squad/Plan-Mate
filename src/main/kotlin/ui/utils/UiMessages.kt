package org.example.ui.utils


enum class UiMessages(val message: String) {
    TASK_ID_PROMPT("Enter task ID:"),
    TASK_NAME_PROMPT("Enter task name:"),
    TASK_DESCRIPTION_PROMPT("Enter task description:"),
    TASK_STATE_PROMPT("Enter task state name:"),
    INVALID_TASK_STATE_INPUT("Invalid task state input"),
    TASK_CREATE_SUCCESSFULLY("Task created successfully."),
    TASK_EDIT_SUCCESSFULLY("Task updated successfully."),
    EMPTY_TASK_INPUT("Inputs are empty"),
    USER_NAME_PROMPT(""),
    NO_TASK_FOUNDED("No Tasks founded"),
    TASK_DELETE_SUCCESSFULLY("Task deleted successfully."),
    EMPTY_TASK_ID_INPUT("No task ID was provided."),
    EMPTY_TASK_NAME_INPUT("Task name cannot be empty."),
    EMPTY_TASK_DESCRIPTION_INPUT("Task description cannot be empty."),
    EMPTY_TASK_STATE_INPUT("Task state cannot be empty."),
    INVALID_STATE_NAME("Invalid state name. No matching state ID found."),
    INVALID_OPTION("Invalid option. Please choose a valid option from the menu."),
    GOODBYE("Goodbye! Thank you for using the Task Manager."),
    NO_TASKS_FOUND_IN_PROJECT("No task found in project"),
    EMPTY_PROJECT_ID_INPUT("Empty project input"),
    PROJECT_ID_PROMPT("Enter Project id:");
    override fun toString(): String = message
}