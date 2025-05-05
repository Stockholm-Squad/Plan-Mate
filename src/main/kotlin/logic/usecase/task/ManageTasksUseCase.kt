package org.example.logic.usecase.task

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import logic.model.entities.Task
import org.example.data.utils.DateHandlerImp
import org.example.logic.model.exceptions.*
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.audit.AddAuditSystemUseCase
import java.util.UUID
import kotlin.Result


class ManageTasksUseCase(
    private val taskRepository: TaskRepository,
    private val auditSystem: AddAuditSystemUseCase,
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

    fun createTask(task: Task): Result<Boolean> =
        taskRepository.addTask(task).fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(NoTasksCreated()) }
        )

    fun editTask(updatedTask: Task, userId: UUID): Result<Boolean> =
        taskRepository.editTask(updatedTask).fold(
            onSuccess = {
                if(it){
                    val audits = listOf(AuditSystem(
                        entityType = EntityType.TASK,
                        description = "update task ${updatedTask.name}",
                        userId = userId,
                        dateTime = DateHandlerImp().getCurrentDateTime(),
                        entityTypeId = updatedTask.id
                    ))
                    auditSystem.addAuditsEntries(audits)
                }
                Result.success(it)
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

}