package org.example.data.source

import data.dto.TaskDto

interface TaskDataSource {
    suspend fun getAllTasks(): List<TaskDto>
    suspend fun addTask(task: TaskDto): Boolean
    suspend fun updateTask(task: TaskDto): Boolean
    suspend fun deleteTask(id: String): Boolean
    suspend fun getTasksInProject(projectId: String): List<TaskDto>
    suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto>
    suspend fun addTaskInProject(projectId: String, taskId: String): Boolean
    suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean
    suspend fun getAllTasksByUserName(username: String): List<TaskDto>
}
