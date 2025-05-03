package org.example.logic.usecase.task

import logic.model.entities.Task
import org.example.logic.model.exceptions.NoTasksCreated
import org.example.logic.model.exceptions.NoTasksDeleted
import org.example.logic.model.exceptions.NoTasksFound
import org.example.logic.model.exceptions.TaskNotFoundException
import org.example.logic.repository.TaskRepository
import java.util.*


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

    fun getTasksInProject(projectId: UUID): Result<List<Task>> {
        return taskRepository.getTasksInProject(projectId)
    }

    fun addTaskInProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskRepository.addTaskInProject(projectId, taskId)
    }

    fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskRepository.deleteTaskFromProject(projectId, taskId)
    }


    fun getAllTasksByUserName(userName: String): Result<List<Task>> {
        return taskRepository.getAllTasksByUserName(userName)
    }


}