package org.example.logic.usecase.task

import logic.models.entities.Task
import logic.models.exceptions.NoTasksCreated
import logic.models.exceptions.NoTasksDeleted
import logic.models.exceptions.NoTasksFound
import logic.models.exceptions.TaskNotFoundException
import org.example.logic.repository.TaskRepository
import java.util.*


class ManageTasksUseCase(private val taskRepository: TaskRepository) {

    fun getAllTasks(): Result<List<Task>> =
        taskRepository.getAllTasks().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(NoTasksFound()) }
        )

    fun getTaskByName(taskName: String): Result<Task> {
        return runCatching {
            taskRepository.getAllTasks()
        }.fold(
            onSuccess = { result ->
                result.fold(
                    onSuccess = { tasks ->
                        tasks.find { it.name.equals(taskName, ignoreCase = true) }
                            ?.let { Result.success(it) }
                            ?: Result.failure(TaskNotFoundException())
                    },
                    onFailure = { Result.failure(TaskNotFoundException()) }
                )
            },
            onFailure = { Result.failure(TaskNotFoundException()) }
        )
    }

    fun getTaskIdByName(taskName: String): Result<UUID> {
        return getTaskByName(taskName).map { it.id }
    }

    fun createTask(task: Task): Result<Boolean> =
        taskRepository.addTask(task).fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(NoTasksCreated()) }
        )

    fun editTask(updatedTask: Task): Result<Boolean> =
        taskRepository.editTask(updatedTask).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(NoTasksFound()) }
        )

    fun deleteTaskByName(taskName: String): Result<Boolean> {
        return getTaskIdByName(taskName).fold(
            onSuccess = { uuid ->
                taskRepository.deleteTask(uuid).fold(
                    onSuccess = { Result.success(it) },
                    onFailure = { Result.failure(NoTasksDeleted()) }
                )
            },
            onFailure = { Result.failure(TaskNotFoundException()) }
        )
    }

}