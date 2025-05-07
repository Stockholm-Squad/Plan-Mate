package org.example.logic.usecase.project

import logic.models.entities.Task
import org.example.logic.repository.TaskRepository
import java.util.*

class ManageTasksInProjectUseCase(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val taskRepository: TaskRepository
) {

    suspend fun getTasksInProjectByName(projectName: String): List<Task> {
        return getProjectsUseCase.getProjectByName(projectName).let { project ->
            taskRepository.getTasksInProject(project.id)
        }
    }

    suspend fun getTasksInProjectById(projectId: UUID): List<Task> {
        return taskRepository.getTasksInProject(projectId)
    }

    suspend fun addTaskToProject(projectId: UUID, taskId: UUID): Boolean {
        return taskRepository.addTaskInProject(projectId = projectId, taskId = taskId)
    }

    suspend fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Boolean {
        return taskRepository.deleteTaskFromProject(projectId, taskId)
    }

    suspend fun getAllTasksByUserName(userName: String): List<Task> {
        return taskRepository.getAllTasksByUserName(userName)
    }

}