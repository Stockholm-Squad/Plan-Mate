package org.example.logic.repository

import logic.model.entities.Task
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject

interface TaskRepository {
    fun getAllTasks(): Result<List<Task>>
    fun createTask(task: Task): Result<Boolean>
    fun editTask(task: Task): Result<Boolean>
    fun deleteTask(id: String?): Result<Boolean>
    fun getAllMateTaskAssignment(mateName: String): Result<List<MateTaskAssignment>>
}