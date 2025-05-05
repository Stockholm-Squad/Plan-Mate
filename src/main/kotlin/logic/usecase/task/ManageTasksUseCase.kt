package org.example.logic.usecase.task

import logic.models.entities.AuditSystem
import logic.models.entities.EntityType
import org.example.data.utils.DateHandlerImp
import logic.models.entities.Task
import logic.models.exceptions.NoTasksCreated
import logic.models.exceptions.NoTasksDeleted
import logic.models.exceptions.NoTasksFound
import logic.models.exceptions.TaskNotFoundException
import org.example.logic.repository.TaskRepository
import org.example.logic.repository.AuditSystemRepository
import java.util.*


class ManageTasksUseCase(
    private val taskRepository: TaskRepository,
    private val auditSystemRepository: AuditSystemRepository,
) {

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

    fun createTask(task: Task, userId: UUID): Result<Boolean> =
        taskRepository.addTask(task).fold(
            onSuccess = { isCreates ->
                if (isCreates) logAudit(task, userId)
                Result.success(true)
            },
            onFailure = { Result.failure(NoTasksCreated()) }
        )

    fun editTask(updatedTask: Task, userId: UUID): Result<Boolean> =
        taskRepository.editTask(updatedTask).fold(
            onSuccess = { isUpdated ->
                if (isUpdated) logAudit(updatedTask, userId)
                Result.success(isUpdated)
            },
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

    private fun logAudit(updatedTask: Task, userId: UUID) {
        val auditEntry = AuditSystem(
            entityType = EntityType.TASK,
            description = "update task ${updatedTask.name}",
            userId = userId,
            dateTime = DateHandlerImp().getCurrentDateTime(),
            entityTypeId = updatedTask.id
        )
        auditSystemRepository.addAuditsEntries(listOf(auditEntry))
    }

}