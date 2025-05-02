package org.example.logic.repository

import logic.model.entities.Task

interface TaskRepository {
    fun getAllTasks(): Result<List<Task>>
    fun createTask(task: Task): Result<Boolean>
    fun editTask(task: Task): Result<Boolean>
    fun deleteTask(id: String?): Result<Boolean>


    fun getTasksInProject(projectId: String): Result<List<Task>>
    fun addTaskInProject(projectId: String, taskId: String): Result<Boolean>
    fun deleteTaskFromProject(projectId: String, taskId: String): Result<Boolean>


    fun getAllMateTaskAssignment(mateName: String): Result<List<Task>>
    fun getAllTaskByMateId(mateId: String): Result<List<Task>>
    fun getAllMateByTaskId(taskId: String): Result<List<String>>
    fun addMateTaskAssignment(mateName: String, taskId: String): Result<Boolean>
    fun deleteMateTaskAssignment(mateName: String, taskId: String): Result<Boolean>
}