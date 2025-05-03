package org.example.logic.usecase.task

import logic.model.entities.Task
import org.example.logic.model.exceptions.*
import org.example.logic.repository.TaskRepository
import java.util.UUID
import kotlin.Result


class ManageTasksUseCase(private val taskRepository: TaskRepository) {

    fun getAllTasks(): Result<List<Task>> =
        taskRepository.getAllTasks().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(NoTasksFound()) }
        )

    fun getTaskById(taskId: UUID?): Result<Task> =
        taskRepository.getAllTasks().fold(
            onSuccess = { tasks ->
                tasks.find { it.id == taskId }
                    ?.let { Result.success(it) }
                    ?: Result.failure(TaskNotFoundException())
            },
            onFailure = { Result.failure(TaskNotFoundException()) }
        )

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

    fun deleteTask(taskId: UUID?): Result<Boolean> =
        getTaskById(taskId).fold(
            onSuccess = {
                taskRepository.deleteTask(taskId).fold(
                    onSuccess = { Result.success(it) },
                    onFailure = { Result.failure(NoTasksDeleted()) }
                )
            },
            onFailure = { Result.failure(TaskNotFoundException()) }
        )



}