package org.example.ui.features.task

import logic.models.entities.Task
import org.example.ui.features.common.ui_launcher.UiLauncher

interface TaskManagerUi : UiLauncher {
    fun showAllTasks()
    fun getTaskByName()
    fun createTask()
    fun editTask()
    fun deleteTask()
    fun showAllTasksInProject(): List<Task>
    fun showAllMateTaskAssignment()
}