package org.example.logic.usecase.task

import logic.model.entities.Task
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject
import org.example.logic.repository.TaskRepository

class ManageTasksUseCase(private val taskRepository: TaskRepository) {

    fun getAllTasks(): Result<List<Task>> =
        taskRepository.getAllTasks()

    fun getTaskById(taskId: String): Result<Task> =
        taskRepository.getAllTasks().fold(
            onSuccess = { tasks ->
                tasks.find { it.id == taskId }
                    ?.let { Result.success(it) }
                    ?: Result.failure(Throwable("Task with ID $taskId not found"))
            },
            onFailure = { exception -> Result.failure(exception) }
        )

    fun createTask(task: Task): Result<Boolean> =
        taskRepository.getAllTasks().fold(
            onSuccess = { tasks ->
                if (tasks.any { it.id == task.id }) {
                    Result.failure(Throwable("Task with ID ${task.id} already exists"))
                } else {
                    taskRepository.createTask(task)
                }
            },
            onFailure = { exception -> Result.failure(exception) }
        )

    fun editTask(task: Task): Result<Boolean> =
        taskRepository.getAllTasks().fold(
            onSuccess = { tasks ->
                if (tasks.none { it.id == task.id }) {
                    Result.failure(Throwable("Task with ID ${task.id} not found"))
                } else {
                    taskRepository.editTask(task)
                }
            },
            onFailure = { exception -> Result.failure(exception) }
        )

    fun deleteTask(taskId: String): Result<Boolean> =
        taskRepository.getAllTasks().fold(
            onSuccess = { tasks ->
                if (tasks.none { it.id == taskId }) {
                    Result.failure(Throwable("Task with ID $taskId not found"))
                } else {
                    taskRepository.deleteTask(taskId)
                }
            },
            onFailure = { exception -> Result.failure(exception) }
        )

    fun getAllTasksByProjectId(projectId: String): Result<List<TaskInProject>> =
        taskRepository.getAllTasksByProjectId(projectId).fold(
            onSuccess = { tasks ->
                tasks.filter { it.projectId == projectId }
                    .takeIf { it.isNotEmpty() }
                    ?.let { Result.success(it) }
                    ?: Result.failure(Throwable("No tasks found for project ID $projectId"))
            },
            onFailure = { exception -> Result.failure(exception) }
        )

    fun getAllTasksByUserId(useName: String): Result<List<MateTaskAssignment>> =
        taskRepository.getAllMateTaskAssignment(useName).fold(
            onSuccess = { tasks ->
                tasks.filter { it.user == useName }
                    .takeIf { it.isNotEmpty() }
                    ?.let { Result.success(it) }
                    ?: Result.failure(Throwable("No tasks found for user ID $useName"))
            },
            onFailure = { exception -> Result.failure(exception) }
        )
}