package org.example.logic.usecase.project

import logic.models.entities.Task
import logic.models.exceptions.TaskExceptions
import org.example.logic.repository.TaskRepository
import java.util.*

class ManageTasksInProjectUseCase(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val taskRepository: TaskRepository
) {

    suspend fun getTasksInProjectByName(projectName: String): List<Task> {
        return getProjectsUseCase.getProjectByName(projectName).let { project ->
            taskRepository.getTasksInProject(project.id).takeIf { it.isNotEmpty() }
                ?: throw TaskExceptions.TasksNotFoundException()
        }
    }

    suspend fun getTasksInProjectById(projectId: UUID): List<Task> {
        return taskRepository.getTasksInProject(projectId).takeIf { it.isNotEmpty() }
            ?: throw TaskExceptions.TasksNotFoundException()
    }

    suspend fun addTaskToProject(projectId: UUID, taskId: UUID): Boolean =
        taskRepository.addTaskInProject(projectId, taskId).also { isAdded ->
            if (!isAdded) throw TaskExceptions.TaskNotAddedException()
        }

    suspend fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Boolean {
        return taskRepository.deleteTaskFromProject(projectId, taskId).also { isDeleted ->
            if (!isDeleted) throw TaskExceptions.TaskNotDeletedException()
        }
    }

    suspend fun getAllTasksByUserName(userName: String): List<Task> {
        return taskRepository.getAllTasksByUserName(userName).takeIf { it.isNotEmpty() }
            ?: throw TaskExceptions.TasksNotFoundException()
    }

}