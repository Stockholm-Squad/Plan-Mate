package org.example.logic.usecase.task

import logic.models.entities.AuditSystem
import logic.models.entities.EntityType
import logic.models.entities.Task
import logic.models.exceptions.TaskExceptions
import org.example.data.utils.DateHandlerImp
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.repository.TaskRepository
import java.util.*

class ManageTasksUseCase(
    private val taskRepository: TaskRepository,
    private val auditSystemRepository: AuditSystemRepository,
) {
    suspend fun getAllTasks(): List<Task> {
        return taskRepository.getAllTasks().takeIf { it.isNotEmpty() }
            ?: throw TaskExceptions.TasksNotFoundException()
    }

    suspend fun getTaskByName(taskName: String): Task =
        taskRepository.getAllTasks().find { it.name.equals(taskName, ignoreCase = true) }
            ?: throw TaskExceptions.TaskNotFoundException()

    suspend fun getTaskIdByName(taskName: String): UUID =
        getTaskByName(taskName).id

    suspend fun addTask(task: Task, userId: UUID, projectId: UUID): Boolean {
        val isDuplicate = taskRepository.getTasksInProject(projectId = projectId)
            .filter { it.name.equals(task.name, ignoreCase = true) } !== emptyList<Task>()

        if (isDuplicate) throw TaskExceptions.DuplicateTaskNameException()

        return taskRepository.addTask(task).also { isAdded ->
            if (isAdded) logAudit(task, userId)
            else throw TaskExceptions.TaskNotAddedException()
        }
    }

    suspend fun editTask(updatedTask: Task, userId: UUID): Boolean =
        taskRepository.editTask(updatedTask).also { isUpdated ->
            if (isUpdated) logAudit(updatedTask, userId)
            else throw TaskExceptions.TaskNotEditException()
        }

    suspend fun deleteTaskByName(taskName: String): Boolean =
        taskRepository.deleteTask(getTaskIdByName(taskName)).also { isDeleted ->
            if (!isDeleted) throw TaskExceptions.TaskNotDeletedException()
        }

    private suspend fun logAudit(updatedTask: Task, userId: UUID) {
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
