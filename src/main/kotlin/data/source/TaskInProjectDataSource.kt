package org.example.data.source

import data.dto.TaskInProjectDto


interface TaskInProjectDataSource {
    suspend fun addTaskInProject(projectId: String, taskId: String): Boolean
    suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean
    suspend fun getAllTasksInProject(): List<TaskInProjectDto>
    suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectDto>
}