package org.example.utils

enum class TaskOptions(val option: Int?) {
    SHOW_ALL_TASKS(1),
    SHOW_TASK_BY_ID(2),
    CREATE_TASK(3),
    EDIT_TASK(4),
    DELETE_TASK(5),
    SHOW_TASKS_BY_PROJECT_ID(6),
    SHOW_MATE_TASK_ASSIGNMENTS(7),
    EXIT(0);
}