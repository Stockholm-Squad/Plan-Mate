package org.example.ui.utils


enum class TaskOptions(val option: Int?, val label: String) {
    SHOW_ALL_TASKS(1, "Show all tasks"),
    SHOW_TASK_BY_ID(2, "Show task by ID"),
    CREATE_TASK(3, "Create task"),
    EDIT_TASK(4, "Edit task"),
    DELETE_TASK(5, "Delete task"),
    SHOW_TASKS_BY_PROJECT_ID(6, "Show tasks by project ID"),
    SHOW_MATE_TASK_ASSIGNMENTS(7, "Show mate task assignments"),
    EXIT(0, "Exit");
}
