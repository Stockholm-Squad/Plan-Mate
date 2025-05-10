package org.example.data.source

import data.dto.TaskModel

interface TaskDataSource {
    suspend fun getAllTasks(): List<TaskModel>
    suspend fun addTask(task: TaskModel): Boolean
    suspend fun editTask(task: TaskModel): Boolean
    suspend fun deleteTask(id: String): Boolean
    suspend fun getTasksInProject(taskIds: List<String>): List<TaskModel>
    suspend fun getTasksByIds(taskIds: List<String>): List<TaskModel>
}
