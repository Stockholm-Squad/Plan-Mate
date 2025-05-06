package org.example.logic.repository

import logic.models.entities.Task
import java.util.*

interface TaskRepository {
    suspend fun getAllTasks(): List<Task>
    suspend fun addTask(task: Task): Boolean
    suspend fun editTask(task: Task): Boolean
    suspend fun deleteTask(id: UUID?): Boolean
    suspend fun getTasksInProject(projectId: UUID): List<Task>
    suspend fun addTaskInProject(projectId: UUID, taskId: UUID): Boolean
    suspend fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Boolean
    suspend fun getAllTasksByUserName(userName: String): List<Task>
}