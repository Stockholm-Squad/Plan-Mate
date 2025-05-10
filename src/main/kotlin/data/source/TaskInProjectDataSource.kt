package org.example.data.source

import data.models.TaskInProjectModel


interface TaskInProjectDataSource {
    suspend fun addTaskInProject(projectId: String, taskId: String): Boolean
    suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean
    suspend fun getAllTasksInProject(): List<TaskInProjectModel>
    suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectModel>
}