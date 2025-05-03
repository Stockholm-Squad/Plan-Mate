package org.example.ui.utils

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
        | 1. Show All Audit Entries               |
        | 2. Show Audit Logs by Entity ID         |
        | 3. Exit                                 |
    """.trimIndent()
    const val PROMPT_ENTITY_ID = "Enter entity ( project - task ) ID: "


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