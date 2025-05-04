package org.example.ui.features.common.utils

object UiMessages {
    const val TASK_ID_PROMPT = "Enter task ID:"
    const val TASK_NAME_PROMPT = "Enter task name:"
    const val TASK_DESCRIPTION_PROMPT = "Enter task description:"
    const val TASK_STATE_PROMPT = "Enter task state name:"
    const val INVALID_TASK_STATE_INPUT = "Invalid task state input"
    const val TASK_CREATE_SUCCESSFULLY = "Task created successfully."
    const val TASK_EDIT_SUCCESSFULLY = "Task updated successfully."
    const val EMPTY_TASK_INPUT = "Inputs are empty"
    const val USER_NAME_PROMPT = ""
    const val NO_TASK_FOUNDED = "No Tasks founded"
    const val TASK_DELETE_SUCCESSFULLY = "Task deleted successfully."
    const val EMPTY_TASK_ID_INPUT = "No task ID was provided."
    const val EMPTY_TASK_NAME_INPUT = "Task name cannot be empty."
    const val EMPTY_TASK_DESCRIPTION_INPUT = "Task description cannot be empty."
    const val EMPTY_TASK_STATE_INPUT = "Task state cannot be empty."
    const val INVALID_STATE_NAME = "Invalid state name. No matching state ID found."
    const val INVALID_OPTION = "Invalid option. Please choose a valid option from the menu."
    const val GOODBYE = "Goodbye! Thank you for using the Task Manager."
    const val NO_TASKS_FOUND_IN_PROJECT = "No task found in project"
    const val EMPTY_PROJECT_ID_INPUT = "Empty project input"
    const val PROJECT_NAME_PROMPT = "Enter project name:"
    const val EMPTY_PROJECT_NAME_INPUT = "Invalid empty project name"
    const val PLEASE_SELECT_OPTION = "Please select an option:"
    const val Y = "yes"
    const val N = "no"
    const val INVALID_SELECTION_MESSAGE = "Invalid selection. Please choose a valid number."
    const val SEARCH_AGAIN_PROMPT = "\nWould you like to search again? (y/n):"
    const val EXITING = "Exiting"

    //audit system constants
    val SHOW_AUDIT_SYSTEM_OPTIONS = """
        |----------- Audit System Menu -----------|
        | 1. Show Project Audits                  |
        | 2. Show Task Audits                     |
        | 3. Show Audit Logs by Entity ID         |
        | 4. Exit                                 |
    """.trimIndent()
    const val PROMPT_TASK_NAME = "Enter Task name: "
    const val PROMPT_PROJECT_NAME = "Enter Project Name: "



    val MAIN_MENU_WELCOME_MESSAGE_FOR_ADMIN = """
        |----------- Welcome to planMate System ^_^ -----------|
        |-------  Please choose what do you want to do  -------|
        | 1. Mange projects                                    |
        | 2. Manage tasks                                      |
        | 3. Manage states                                     |
        | 4. Add Mate                                          |
        | 5. Show Audit system                                 |
        | 6. Log out                                           |
    """.trimIndent()

    val MAIN_MENU_WELCOME_MESSAGE_FOR_MATE = """
        |----------- Welcome to planMate System ^_^ -----------|
        |-------  Please choose what do you want to do  -------|
        | 1. Manage tasks                                      |
        | 2. Manage states                                     |
        | 3. Show Audit system                                 |
        | 4. Log out                                           |
    """.trimIndent()
}