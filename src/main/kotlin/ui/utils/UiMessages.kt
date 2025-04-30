package org.example.ui.utils

enum class UiMessages(val message: String) {
    TASK_ID_PROMPT("Enter task ID:"),
    TASK_ID_EMPTY("Task ID cannot be empty."),
    TASK_NOT_FOUND("Error: Task with ID %s not found."),
    TASK_DETAILS("Task Details:"),
    TASK_CREATED_SUCCESS("Task created successfully!"),
    TASK_CREATION_ERROR("Error creating task: %s"),
    TASK_UPDATED_SUCCESS("Task updated successfully!"),
    TASK_UPDATE_ERROR("Error updating task: %s"),
    TASK_DELETED_SUCCESS("Task deleted successfully!"),
    TASK_DELETION_ERROR("Error deleting task: %s"),
    ENTER_TASK_NAME("Enter task name:"),
    TASK_NAME_EMPTY("Task name cannot be empty."),
    ENTER_TASK_DESCRIPTION("Enter task description:"),
    TASK_DESCRIPTION_EMPTY("Task description cannot be empty."),
    ENTER_STATE_ID("Enter state ID:"),
    STATE_ID_EMPTY("State ID cannot be empty."),
    ENTER_PROJECT_ID("Enter project ID:"),
    PROJECT_ID_EMPTY("Project ID cannot be empty."),
    NO_TASKS_FOUND("No tasks found."),
    ALL_TASKS("All Tasks:"),
    INVALID_OPTION("😕 Oops! That’s not on the options. Pick a number between 0 and 7!"),
    GOODBYE("👋 Goodbye"),
    TASK_ASSIGNMENT_PROMPT("Enter user ID (mate ID):"),
    TASK_ASSIGNMENT_EMPTY("User ID cannot be empty."),
    NO_TASKS_FOR_USER("No tasks found for user ID: %s"),
    TASKS_FOR_USER("Tasks assigned to user ID %s:"),
    PROJECT_TASKS("Tasks in project '%s':"),
    NO_PROJECT_TASKS("No tasks found for project '%s'."),
    STATES_FOR_PROJECT("States for project '%s':"),
    NO_STATES_FOR_PROJECT("No states found for project '%s'."),
    MENU_HEADER("""
        ========================= Tasks Option =========================
        Please Choose an option. Pick a number between 0 and 7!

        1. Show all Tasks 
        2. Get task by id 
        3. Create task
        4. Edit task
        5. Delete Task
        6. Show all tasks at specific project
        7. Show all tasks assignment to user
        0. Exit (Don't gooo! 😢)
        -----------------------------------------------------
        Choose an option: 
    """);

    override fun toString() = message
}
