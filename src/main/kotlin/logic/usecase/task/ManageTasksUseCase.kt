package org.example.logic.usecase.task

import logic.model.entities.Task
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject
import org.example.logic.repository.TaskRepository
import kotlin.String

class ManageTasksUseCase(private val taskRepository: TaskRepository) {

    fun getAllTasks(): Result<List<Task>> =
        taskRepository.getAllTasks().fold(
            onSuccess = { Result.success(it) },
            onFailure = { exception -> Result.failure(exception) }
        )

    fun getTaskById(taskId: String?): Result<Task> =
        taskRepository.getAllTasks().fold(
            onSuccess = { tasks ->
                tasks.find { it.id == taskId }
                    ?.let { Result.success(it) }
                    ?: Result.failure(Throwable("Task with ID $taskId not found"))
            },
            onFailure = { exception -> Result.failure(exception) }
        )

    fun createTask(task: Task): Result<Boolean> {
        return taskRepository.createTask(task).fold(
            onSuccess = { Result.success(it) },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    fun editTask(updatedTask: Task): Result<Boolean> =
        getTaskById(updatedTask.id).fold(
            onSuccess = { existingTask ->
                taskRepository.editTask(updatedTask)
            },
            onFailure = { exception ->
                Result.failure(Throwable("Failed to find task with ID ${updatedTask.id}: ${exception.message}"))
            }
        )

    fun deleteTask(taskId: String?): Result<Boolean> =
        getTaskById(taskId).fold(
            onSuccess = { existingTask ->
                taskRepository.deleteTask(taskId)
            },
            onFailure = { exception ->
                Result.failure(Throwable("Failed to find task with ID $taskId: ${exception.message}"))
            }
        )

    fun getAllMateTaskAssignment(userName: String): Result<List<MateTaskAssignment>> =
        taskRepository.getAllMateTaskAssignment(userName).fold(
            onSuccess = { Result.success(it) },
            onFailure = { exception -> Result.failure(exception) }
        )

    fun getAllTasksByProjectId(projectId: String): Result<List<TaskInProject>> =
        taskRepository.getAllTasksByProjectId(projectId).fold(
            onSuccess = { Result.success(it) },
            onFailure = { exception -> Result.failure(exception) }
        )

    fun getAllStatesByProjectId(projectId: String): Result<List<Pair<String, String>>> =
        taskRepository.getAllTasksByProjectId(projectId).fold(
            onSuccess = { tasks ->
                val states = tasks.map { Pair(it.taskId, it.stateId) }
                Result.success(states)
            },
            onFailure = { exception -> Result.failure(exception) }
        )
}