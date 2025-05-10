package org.example.logic.usecase.task

import org.example.data.utils.DateHandlerImp
import org.example.logic.*
import org.example.logic.entities.AuditSystem
import org.example.logic.entities.EntityType
import org.example.logic.entities.Task
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.repository.TaskRepository
import java.util.*

class ManageTasksUseCase(
    private val taskRepository: TaskRepository,
    private val auditSystemRepository: AuditSystemRepository,
) {
    suspend fun getAllTasks(): List<Task> {
        return taskRepository.getAllTasks().takeIf { it.isNotEmpty() }
            ?: throw TasksNotFoundException()
    }

    suspend fun getTaskByName(taskName: String): Task =
        taskRepository.getAllTasks().find { it.name.equals(taskName, ignoreCase = true) }
            ?: throw TaskNotFoundException()

    suspend fun getTaskIdByName(taskName: String): UUID =
        getTaskByName(taskName).id

    suspend fun addTask(task: Task, userId: UUID, projectId: UUID): Boolean {
        val isDuplicate = taskRepository.getTasksInProject(projectId = projectId)
            .filter { it.name.equals(task.name, ignoreCase = true) } !== emptyList<Task>()

        if (isDuplicate) throw DuplicateTaskNameException()

        return taskRepository.addTask(task).also { isAdded ->
            if (isAdded) logAudit(task, userId)
            else throw TaskNotAddedException()
        }
    }

    suspend fun editTask(updatedTask: Task, userId: UUID): Boolean =
        taskRepository.editTask(updatedTask).also { isUpdated ->
            if (isUpdated) logAudit(updatedTask, userId)
            else throw TaskNotEditException()
        }

    suspend fun deleteTaskByName(taskName: String): Boolean =
        taskRepository.deleteTask(getTaskIdByName(taskName)).also { isDeleted ->
            if (!isDeleted) throw TaskNotDeletedException()
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
