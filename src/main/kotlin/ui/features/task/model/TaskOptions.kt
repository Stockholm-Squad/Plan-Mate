package org.example.ui.features.task.model

enum class TaskOptions(val option: Int?, val label: String) {
    SHOW_ALL_TASKS(1, "Show all tasks"),
    SHOW_TASK_BY_NAME(2, "Show task by name"),
    ADD_TASK(3, "Add task"),
    UPDATE_TASK(4, "Update task"),
    DELETE_TASK(5, "Delete task"),
    SHOW_TASKS_BY_PROJECT_NAME(6, "Show tasks by project name"),
    SHOW_MATE_TASK_ASSIGNMENTS(7, "Show mate task assignments"),
    EXIT(0, "Exit")
}