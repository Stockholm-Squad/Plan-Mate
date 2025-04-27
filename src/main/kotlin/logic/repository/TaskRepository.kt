package org.example.logic.repository

import logic.model.entities.Task

interface TaskRepository {

    fun getTaskById(id: String): Result<Task>
    fun addTask(task: Task): Result<Boolean>
    fun editTask(task: Task): Result<Boolean>
    fun deleteTask(id: String): Result<Boolean>
    fun getAllTasks(): Result<List<Task>>
    fun getAllTasksByProjectId(projectId: String): Result<List<Task>>
    fun getAllTasksByUserId(userId: String): Result<List<Task>>
}