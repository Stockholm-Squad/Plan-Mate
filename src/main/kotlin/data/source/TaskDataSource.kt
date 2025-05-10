package org.example.data.source

import data.dto.TaskDto

interface TaskDataSource {
    suspend fun getAllTasks(): List<TaskDto>
    suspend fun addTask(task: TaskDto): Boolean
    suspend fun editTask(task: TaskDto): Boolean
    suspend fun deleteTask(id: String): Boolean
    suspend fun getTasksInProject(taskIds: List<String>): List<TaskDto>
    suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto>
}
