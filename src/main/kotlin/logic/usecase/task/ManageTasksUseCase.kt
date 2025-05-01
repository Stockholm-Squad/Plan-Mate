package org.example.logic.usecase.task

import logic.model.entities.Task
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.TaskRepository
import kotlin.Result
import kotlin.String


class ManageTasksUseCase(private val taskRepository: TaskRepository) {

    fun getAllTasks(): Result<List<Task>> =
        taskRepository.getAllTasks().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoTasksFound()) }
        )

    fun getTaskById(taskId: String): Result<Task> =
        taskRepository.getAllTasks().fold(
            onSuccess = { tasks ->
                tasks.find { it.id == taskId }
                    ?.let { Result.success(it) }
                    ?: Result.failure(PlanMateExceptions.LogicException.TaskNotFoundException())
            },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.TaskNotFoundException()) }
        )

    fun createTask(task: Task): Result<Boolean> =
        taskRepository.createTask(task).fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoTasksCreated()) }
        )

    fun editTask(updatedTask: Task): Result<Boolean> =
        taskRepository.editTask(updatedTask).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.DidNotUpdateProject()) }
        )

    fun deleteTask(taskId: String): Result<Boolean> =
        getTaskById(taskId).fold(
            onSuccess = {
                taskRepository.deleteTask(taskId).fold(
                    onSuccess = { Result.success(true) },
                    onFailure = { Result.failure(PlanMateExceptions.LogicException.NoTasksDeleted()) }
                )
            },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.TaskNotFoundException()) }
        )
}
