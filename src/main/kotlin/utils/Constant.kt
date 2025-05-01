package org.example.utils

object Constant {
    const val PLEASE_SELECT_OPTION = "Please select an option:"
    const val Y = "yes"
    const val N = "no"
    const val INVALID_SELECTION_MESSAGE = "Invalid selection. Please choose a valid number."
    const val SEARCH_AGAIN_PROMPT = "\nWould you like to search again? (y/n):"
    const val EXITING = "Exiting"

    //audit system constants
    val SHOW_AUDIT_SYSTEM_OPTIONS = """
        |----------- Audit System Menu -----------|
        | 1. Show Audit Logs by Task ID           |
        | 2. Show Audit Logs by Project ID        |
        | 3. Show Audit Logs by Username          |
        | 4. Show Audit Logs by Audit System ID   |
        | 5. Show All Audit Entries               |
        | 6. Exit                                 |
    """.trimIndent()

    const val PROMPT_PROJECT_ID = "Enter project ID: "
    const val PROMPT_TASK_ID = "Enter task ID: "
    const val PROMPT_USERNAMES = "Enter Username: "
    const val PROMPT_AUDIT_SYSTEM_ID = "Enter Audit System ID: "
}