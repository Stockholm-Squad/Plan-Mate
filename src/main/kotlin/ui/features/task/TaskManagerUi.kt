package org.example.ui.features.task

import org.example.ui.features.common.ui_launcher.UiLauncher

interface TaskManagerUi : UiLauncher {
    suspend fun showAllTasks()
    suspend fun getTaskByName()
    suspend fun createTask()
    suspend fun editTask()
    suspend fun deleteTask()
    suspend fun showAllTasksInProject()
    suspend fun showAllMateTaskAssignment()
}