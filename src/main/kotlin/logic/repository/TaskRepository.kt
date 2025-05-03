package org.example.logic.repository

import logic.model.entities.Task
import logic.model.entities.User
import java.util.*

interface TaskRepository {
    fun getAllTasks(): Result<List<Task>>
    fun addTask(task: Task): Result<Boolean>
    fun editTask(task: Task): Result<Boolean>
    fun deleteTask(id: UUID?): Result<Boolean>
    fun getTasksInProject(projectId: UUID): Result<List<Task>>
    fun addTaskInProject(projectId: UUID, taskId: UUID): Result<Boolean>
    fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Result<Boolean>
    fun getAllTasksByUserName(userName: String): Result<List<Task>>
}