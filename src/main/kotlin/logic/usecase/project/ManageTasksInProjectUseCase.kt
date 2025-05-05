package org.example.logic.usecase.project

import logic.models.entities.Task
import org.example.logic.repository.TaskRepository
import java.util.*

class ManageTasksInProjectUseCase(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val taskRepository: TaskRepository
) {

    fun getTasksInProjectByName(projectName: String): Result<List<Task>> {
        return getProjectsUseCase.getProjectByName(projectName).fold(
            onSuccess = { project ->
                taskRepository.getTasksInProject(project.id)
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    fun getTasksInProjectById(projectId: UUID): Result<List<Task>> {
        return taskRepository.getTasksInProject(projectId)
    }

    fun addTaskToProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskRepository.addTaskInProject(projectId = projectId, taskId = taskId)
    }

    fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskRepository.deleteTaskFromProject(projectId, taskId)
    }

    fun getAllTasksByUserName(userName: String): Result<List<Task>> {
        return taskRepository.getAllTasksByUserName(userName)
    }

}