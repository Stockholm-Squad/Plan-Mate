package org.example.ui.features.common.utils

object UiMessages {
    const val TASK_NAME_PROMPT = "Enter task name: "
    const val NEW_TASK_NAME_PROMPT = "Enter new task name: "
    const val TASK_DESCRIPTION_PROMPT = "Enter task description: "
    const val TASK_STATE_PROMPT = "Enter task state name: "
    const val EMPTY_TASK_INPUT = "Inputs are empty"
    const val USER_NAME_PROMPT = "Enter User name: "
    const val PASSWORD_PROMPT = "Enter password: "
    const val NO_PROJECTS_FOUND = "No projects found"
    const val PROJECT_DETAILS = "Project Details:"
    const val NAME = "Name:"
    const val STATE = "State:"
    const val NEW = "new"
    const val CURRENT_STATE = "Current state:"
    const val ENTER_NEW_STATE = "Enter new state Name"
    const val PROJECT_ADDED = "Project added successfully"
    const val PROJECT_UPDATED = "Project updated successfully"
    const val PROJECT_DELETED = "Project deleted successfully"
    const val FAILED_TO_DELETE_PROJECT = "Failed to delete project"
    const val FAILED_TO_UPDATE_PROJECT = "Failed to update project"
    const val PROJECT_DOES_NOT_EXIST = "project doesn't changed"
    const val ENTER_NEW_PROJECT_NAME = "Enter new project name"
    const val ENTER_PROJECT_NAME_TO_DELETE = "Enter project Name to delete"
    const val LEAVE_BLANK_TO_KEEP = "leave blank to keep"
    const val FAILED_TO_ADD_PROJECT = "Failed to add project"
    const val ENTER_PROJECT_NAME_TO_UPDATE = "Enter project Name to update"
    const val YOU_LIKE_TO_ADD_TASKS = "Would you like to add tasks to this project? (Y/N): "
    const val AVAILABLE_STATE = "Available states:"
    const val ENTER_STATE_NAME_OR_NEW_TO_CREATE = "Enter state Name (or 'new' to create a new state)"
    const val TASK_DELETE_SUCCESSFULLY = "Task deleted successfully."
    const val EMPTY_TASK_NAME_INPUT = "Task name cannot be empty."
    const val PROJECT_CAN_NOT_BE_EMPTY = "Project name cannot be empty."
    const val EMPTY_USER_NAME_INPUT = "Empty user name."
    const val INVALID_OPTION = "Invalid option. Please choose a valid option from the menu."
    const val GOODBYE = "Goodbye! Thank you for using the Task Manager."
    const val PROJECT_NAME_PROMPT = "Enter project name: "
    const val EMPTY_PROJECT_NAME_INPUT = "Invalid empty project name"
    const val SELECT_OPTION = "Please select an option: "
    const val Y = "y"
    const val N = "n"
    const val INVALID_SELECTION_MESSAGE = "Invalid selection. Please try again ^_^"
    const val SEARCH_AGAIN_PROMPT = "\nWould you like to search again? (y/n): "
    const val EXITING = "Exiting"
    const val INVALID_USER = "Invalid user"
    const val WHAT_DO_YOU_NEED = "What do you need ^_^"
    const val PLEASE_ENTER_NAME_FOR_THE_STATE = "Please enter name for the state:"
    const val PLEASE_ENTER_STATE_NAME_YOU_WANT_TO_UPDATE = "Please enter the state you want to update: "
    const val STATE_UPDATED_SUCCESSFULLY = "State updated successfully ^_^"
    const val STATE_DELETED_SUCCESSFULLY = "State deleted successfully ^_^"
    const val STATE_ADDED_SUCCESSFULLY = "State added successfully ^_^"
    const val PLEASE_ENTER_THE_NEW_STATE_NAME = "Please enter the new state name: "
    const val PLEASE_ENTER_STATE_NAME_YOU_WANT_TO_DELETE = "Please enter the state you want to delete: "
    const val INVALID_INPUT = "Invalid input"
    const val USERNAME_PASSWORD_CAN_NOT_BE_EMPTY = "Username and password cannot be empty"
    const val USER_NOT_LOGGED_IN = "user not loged in"
    const val INVALID_TASK_NAME_INPUT_DO_YOU_WANT_TO_RETURN_MAIN_MENU =
        "Invalid task name entered.\nPress Enter to return to the main menu or 'Y' to try again."
    const val INVALID_PROJECT_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU =
        "Invalid project name entered.\nPress 'Enter' to return to the main menu or 'Y' to try again."
    const val INVALID_DESCRIPTION_DO_YOU_WANT_TO_RETURN_MAIN_MENU =
        "Invalid description entered.\nPress Enter to return to the main menu or 'Y' to try again."
    const val INVALID_STATE_NAME_DO_YOU_WANT_TO_RETURN_MAIN_MENU =
        "Invalid state name entered.\nPress Enter to return to the main menu or 'Y' to try again."
    const val FAILED_TO_LOAD_AUDITS = "Failed to load audit,"
    const val UNKNOWN_ERROR = "Unknown error!!"
    const val ADD_NEW_USER_FIRST = "Would you like to add a new user first? (y/n): "
    const val ENTER_USER_NAME_TO_ASSIGN_TO_PROJECT = "Enter username to assign or leave it blank to back: "
    const val FAILED_TO_ASSIGN_USER_TO_PROJECT = "Failed to Assign user to project"
    const val PLEASE_TRY_AGAIN = "Please try again ^_^"
    const val ENTER_PROJECT_NAME_TO_VIEW_ASSIGNED_USER =
        "Enter project name to view assigned users (leave blank to cancel): "
    const val USER_ASSIGNED_TO_PROJECT = "User Assigned to project successfully ^_^"
    const val FAILED_LOADING_USER_ASSIGNED_TO_PROJECT = "Filed on loading Users assigned to project"
    const val NO_USERS_ASSIGNED_TO_PROJECT = "No users assigned to this project"
    const val USERS_ASSIGNED_TO = "Users assigned to"
    const val OR_LEAVE_IT_BLANK_TO_BACK = "or leave it blank to back: "
    const val ENTER_USER_NAME_TO_REMOVE_PROJECT = "Enter username to remove from project"
    const val USER_DELETED_FROM_PROJECT = "user deleted from project Successfully ^_^"
    const val FAILED_TO_DELETE_USER_FROM_PROJECT = "Failed to delete user from project"
    const val LOGIN_USER_NAME_PROMPT = "Please enter your user name: "
    const val LOGIN_PASSWORD_PROMPT = "Please enter your Password: "
    const val FAILED_TO_LOAD_STATE = "Failed to load states"
    const val FAILED_TO_DELETE_STATE = "Failed to delete state"
    const val FAILED_TO_UPDATE_STATE = "Failed to update state:"
    const val FAILED_TO_ADD_STATE = "Failed to add state:"
    const val ADDING_NEW_USER = "➕ Adding new user..."
    const val USER_ADDED = "✅ User added successfully!"
    const val FAILED_TO_ADD_USER = "Failed to add user"

    //audit constants
    val SHOW_AUDIT_OPTIONS = """
        |=============== Audit Menu ==============|
        | 1. Show Project Audits                  |
        | 2. Show Task Audits                     |
        | 3. Show All Audits                      |
        | 0. Exit                                 |
        |-----------------------------------------|
    """.trimIndent()
    const val PROMPT_TASK_NAME = "Enter Task name: "
    const val PROMPT_PROJECT_NAME = "Enter Project Name: "


    val MAIN_MENU_WELCOME_MESSAGE_FOR_ADMIN = """
        |=========== Welcome to planMate System ^_^ ===========|
        |=======  Please choose what do you want to do  =======|
        | 1. Mange projects                                    |
        | 2. Manage tasks                                      |
        | 3. Manage states                                     |
        | 4. Add Mate                                          |
        | 5. Add Mate To Project                               |
        | 6. Show Audit                                        |
        | 0. Log out                                           |
        |------------------------------------------------------|
    """.trimIndent()

    val MAIN_MENU_WELCOME_MESSAGE_FOR_MATE = """
        |=========== Welcome to planMate System ^_^ ===========|
        |=======  Please choose what do you want to do  =======|
        | 1. Manage tasks                                      |
        | 2. Manage states                                     |
        | 3. Show Audit                                        |
        | 0. Log out                                           |
        |------------------------------------------------------|
    """.trimIndent()


    val SHOW_ADD_USER_TO_PROJECT_OPTIONS = """
        |=============== Users In Project Management ==============|
        | 1. Assign users to project                               |
        | 2. View users assigned to project                        |
        | 3. Remove user from project                              |
        | 0. Exit                                                  |
        |----------------------------------------------------------|
    """.trimIndent()

    val SHOW_ADMIN_ENTITY_STATE_OPTIONS = """
        |=============== Entity State Management ==================|
        | 1. Show all states                                       |
        | 2. Add state                                             |
        | 3. Update state                                          |
        | 4. Delete state                                          |
        | 0. Exit                                                  |
        |----------------------------------------------------------|
    """.trimIndent()

    val SHOW_PROJECT_MANAGEMENT_OPTIONS = """
        |=================== Project Management ===================|
        | 1. Show all projects                                     |
        | 2. Show project details                                  |
        | 3. Add project                                           |
        | 4. Update project                                        |
        | 5. Delete project                                        |
        | 0. Exit                                                  |
        |----------------------------------------------------------|
    """.trimIndent()

    val SHOW_TASK_MANAGEMENT_OPTIONS = """
        |==================== Tasks Management ====================|
        | 1. Show all tasks                                        |
        | 2. Show task by name                                     |
        | 3. Add task                                              |
        | 4. Update task                                           |
        | 5. Delete task                                           |
        | 6. Show tasks by project name                            |
        | 7. Show mate task assignments                            |
        | 0. Exit                                                  |
        |----------------------------------------------------------|
    """.trimIndent()

}